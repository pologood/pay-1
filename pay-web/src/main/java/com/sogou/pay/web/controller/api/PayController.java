package com.sogou.pay.web.controller.api;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.thirdpay.api.PayPortal;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sogou.pay.common.cache.RedisUtils;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultBean;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.model.ChannelAdaptModel;
import com.sogou.pay.manager.model.CommonAdaptModel;
import com.sogou.pay.manager.payment.AppManager;
import com.sogou.pay.manager.payment.ChannelAdaptManager;
import com.sogou.pay.manager.payment.PayManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.utils.Constant;
import com.sogou.pay.service.utils.DataSignUtil;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;
//import com.sogou.pay.thirdpay.api.PayApi;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.form.PayParams;
import com.sogou.pay.web.utils.ControllerUtil;
import com.sogou.pay.web.utils.ServletUtil;

/**
 * @Author	huangguoqing 
 * @ClassName	PayController 
 * @Date	2015年2月28日 
 * @Description: 支付请求controller
 */
@Controller
//@RequestMapping(value = "/pay")
public class PayController extends BaseController{
    
    private static final Logger logger = LoggerFactory.getLogger(PayController.class);
    
    private static final int SECONDS = 300;//设置缓存时间
    
    private static final String UNDERLINE = "_";
            
    @Autowired
    private PayManager payManager;

    @Autowired
    private ChannelAdaptManager channelAdaptMaanger;
    
    @Autowired
    private AppManager appManager;
    
    @Autowired
    private SecureManager secureManager;
    
//    @Autowired
//    private PayApi payApi;

    @Autowired
    private PayPortal payPortal;
    
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SequenceFactory sequencerGenerator;

    /**
     * @Author	huangguoqing 
     * @MethodName	doPay 
     * @param params 商户请求参数
     * @param request
     * @return ModelAndView
     * @Date	2015年2月28日
     * @Description: 支付请求业务
     *               1.验证签名
     *               2.验证参数
     *               3.生成支付单数据
     *               4.判断参数中是否有支付渠道
     *               5.支付业务处理
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/pay/doPay",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping({"/pay/doPay", "/gw/pay/web"})
    public ModelAndView doPay(PayParams params, HttpServletRequest request){
        ModelAndView view = new ModelAndView("toAgency");
        logger.info("【支付请求】进入dopay,请求参数为：" + JSONUtil.Bean2JSON(params));
        //将参数转化为map
        PMap paramMap = BeanUtil.Bean2PMap(params);
        //获得用户IP
        String ip = ServletUtil.getRealIp(request);
        paramMap.put("userIp", ip);
        paramMap.put("channelCode",params.getBankId());
        /**2.验证参数**/
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() != 0) {
            //验证参数失败，调到错误页面
            logger.error("【支付请求】" + validateResult.toString().substring(1,validateResult.toString().length()-1));
            return setErrorPage(ResultStatus.PAY_PARAM_ERROR, "web");
        }
        //转义商品名称与描述
        paramMap = escapeSequence(paramMap);
        logger.info("【支付请求】通过验证参数！");
        /**1.验证签名**/
        Result signResult = secureManager.verifyAppSign(params);
        if(!Result.isSuccess(signResult)){
          //获取业务平台签名失败,跳到错误页面
          return setErrorPage(signResult.getStatus(),"web");
        }
        logger.info("【支付请求】通过验证签名！");
        /**3.生成支付单信息**/
        //查询该订单是否已经支付
        ResultMap orderResult = payManager.selectPayOrderInfoByOrderId(params.getOrderId(),params.getAppId());
        if(!Result.isSuccess(orderResult)){
            //系统错误或者该支付单已经支付完成,跳到错误页面
            return setErrorPage(orderResult.getStatus(), "web");
        }
        String payId = null;
        if(null != orderResult.getReturnValue()){
            payId = ((PayOrderInfo)orderResult.getReturnValue()).getPayId();
        } else {
            ResultMap payOrderResult = payManager.insertPayOrder(paramMap);
            if(!Result.isSuccess(payOrderResult)){
                //插入支付单失败,跳到错误页面
                return setErrorPage(payOrderResult.getStatus(), "web");
            }
            payId = payOrderResult.getReturnValue().toString();
        }
        logger.info("【支付请求】成功生成支付单信息！支付单号为：" + payId);
        /**4.判断是否有渠道支付信息，若无，则进行银行适配**/
        if(StringUtils.isBlank(params.getBankId())){
            //银行适配
            ResultBean<ChannelAdaptModel> resultBean = channelAdaptMaanger.getChannelAdapt(
                    Integer.parseInt(params.getAppId()), Integer.parseInt(params.getAccessPlatform()));
            if(!Result.isSuccess(resultBean)){
                //获得支付渠道失败
                return setErrorPage(resultBean.getStatus(), "web");
            }
            //获得网银支付渠道、第三方支付渠道、扫码支付渠道
            ChannelAdaptModel adaptModel = resultBean.getValue();
            //网银列表
            List<CommonAdaptModel> commonPayList = adaptModel.getCommonPay4DebitList();
            //第三方支付列表
            List<CommonAdaptModel> payOrgList = adaptModel.getPayOrgList();
            //扫码支付列表
            List<CommonAdaptModel> scanCodeList = adaptModel.getScanCodeList();
            //B2B支付列表
            List<CommonAdaptModel> b2bList = adaptModel.getB2bList();
            if(commonPayList.isEmpty() && payOrgList.isEmpty() && scanCodeList.isEmpty() && b2bList.isEmpty())
                return setErrorPage(ResultStatus.PAY_PARAM_ERROR, "web");
            //获得收款方信息
            Result<App> appResult = appManager.selectAppInfo(Integer.parseInt(params.getAppId()));
            if(!Result.isSuccess(appResult)){
                return setErrorPage(appResult.getStatus(), "web");
            }
            //支付流水号
            String payDetailId = "";
            //微信二维码
            String qrCode = "";
            if(isExistRecommend(scanCodeList)){
                //组装微信二维码
                paramMap.put("payId", payId);
                paramMap.put("channelCode",Constant.WECHAT);
                ResultMap payResult = this.commonPay(paramMap);
                if(!Result.isSuccess(payResult)){
                    return setErrorPage(payResult.getStatus(), "web");
                }
                payDetailId = payResult.getData().get("payDetailId").toString();
                //向第三方支付机构发送支付请求
                try {
                    String url = (String)payResult.getReturnValue();
                    qrCode = getWebChatCode(url);
                    //将weCahtCode缓存到Redis中
                    redisUtils.setWithinSeconds(payId+UNDERLINE+Constant.WECHAT, qrCode,SECONDS);
                } catch (Exception e) {
                    return setErrorPage(ResultStatus.SYSTEM_ERROR, "web");
                }
            }
            //组装需要传递的参数
            Map<String,Object> commonMap = new HashMap<String, Object>();
            commonMap.put("appId", paramMap.get("appId"));
            commonMap.put("accessPlatform", paramMap.get("accessPlatform"));
            commonMap.put("orderAmount", new BigDecimal(paramMap.getString("orderAmount")));
            commonMap.put("payId", payId);
            commonMap.put("userIp", ServletUtil.getRealIp(request));
            commonMap.put("productName", paramMap.get("productName"));
            commonMap.put("companyName", Constant.COMPANYMAP.get(appResult.getReturnValue().getBelongCompany()));
            commonMap.put("payDetailId", payDetailId);
            view.addObject("qrCode",qrCode);
            view.addObject("appUrl",paramMap.get("pageUrl"));
            view.addObject("orderId",params.getOrderId());
            view.addObject("commonPayList",commonPayList);
            view.addObject("payOrgList",payOrgList);
            view.addObject("scanCodeList",scanCodeList);
            view.addObject("b2bList",b2bList);
            view.addObject("commonMap",commonMap);
            //收银台页面
            view.setViewName("cashier");
            return view;
        } 
        /**5.支付业务处理**/
        //将支付单ID放入map
        paramMap.put("payId",payId);
        ResultMap payResult = this.commonPay(paramMap);
        if(!Result.isSuccess(payResult)){
            //支付业务失败,跳到错误页面
            return setErrorPage(payResult.getStatus(), "web");
        }
        //向第三方支付机构发送支付请求
        view.addObject("payUrl",payResult.getReturnValue());
        logger.info("【支付请求】支付请求结束！");
        return view;
    }

    /**
     * @Author	huangguoqing 
     * @MethodName	doCashierPay 
     * @param request
     * @param response
     * @return ModelAndView
     * @Date	2015年3月19日
     * @Description: 收银台确认支付
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/pay/doCashierPay",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("/pay/doCashierPay")
    public ModelAndView doCashierPay( HttpServletRequest request,HttpServletResponse response){
        ModelAndView view = new ModelAndView("toAgency");
        Map<String, String> parameterMap = getRequestParameterMap(request);
        PMap paramMap = new PMap();
        paramMap.putAll(parameterMap);
        paramMap.put("channelCode",parameterMap.get("bankId"));
        //校验支付单信息
        ResultMap orderResult = payManager.checkPayOrderInfo(paramMap);
        if(!ResultMap.isSuccess(orderResult)){
            //校验失败
            return setErrorPage(orderResult.getStatus(), "web");
        }
        //业务处理
        ResultMap payResult = this.commonPay(paramMap);
        if(!Result.isSuccess(payResult)){
            //支付业务失败,跳到错误页面
            return setErrorPage(payResult.getStatus(), "web");
        }
        //向第三方支付机构发送支付请求
        view.addObject("payUrl",payResult.getReturnValue());
        logger.info("【支付请求】支付请求结束！");
        return view;
    }
    
    /**
     * @Author	huangguoqing 
     * @MethodName	getQrCode 
     * @param request
     * @param response
     * @return 扫码支付链接
     * @Date	2015年3月20日
     * @Description:扫码支付链接或微信扫码字符串
     */
    @RequestMapping("/pay/getQrCode")
    @ResponseBody
    public String getQrCode(HttpServletRequest request,HttpServletResponse response){
        ResultMap result = ResultMap.build();
        String channelCode = request.getParameter("channelCode");
        Map<String, String> parameterMap = getRequestParameterMap(request);
        PMap paramMap = new PMap();
        paramMap.putAll(parameterMap);
        String payId = paramMap.getString("payId");
        /**检验订单状态**/
        ResultMap orderResult = payManager.checkPayOrderInfo(paramMap);
        if(!ResultMap.isSuccess(orderResult)){
            //支付单信息不存在
            result.withError(orderResult.getStatus());
            return JSONObject.toJSONString(result);
        }
        //首先从缓存中获取weChatCode
        String qrCode = null;
        if(Constant.ALIPAY.equals(channelCode)){
            qrCode = redisUtils.get(payId+UNDERLINE+Constant.ALIPAY);
            if(null != qrCode){
                result.addItem("qrCode", qrCode);
                return JSONObject.toJSONString(result);
            }   
        } else if (Constant.WECHAT.equals(channelCode)){
            qrCode = redisUtils.get(payId+UNDERLINE+Constant.WECHAT);
            if(null != qrCode){
                result.addItem("qrCode", qrCode);
                return JSONObject.toJSONString(result);
            }
        }
        //获得根据支付ID获取支付单信息
        ResultMap payResult = this.commonPay(paramMap);
        if(!Result.isSuccess(payResult)){
            //支付业务失败
            result.withError(ResultStatus.SYSTEM_ERROR);
            return JSONObject.toJSONString(result);
        }
        try {
            if(Constant.ALIPAY.equals(channelCode)){
                //支付宝扫码支付
                qrCode = (String)payResult.getReturnValue();
                redisUtils.setWithinSeconds(payId+UNDERLINE+channelCode, qrCode,SECONDS);
                result.addItem("qrCode", qrCode);
            } else {
                //向第三方支付机构发送支付请求
                String url = (String)payResult.getReturnValue();
                qrCode = getWebChatCode(url);
                //将weCahtCode缓存到Redis中
                redisUtils.setWithinSeconds(payId+UNDERLINE+channelCode, qrCode,SECONDS);
                result.addItem("qrCode", qrCode);
                result.addItem("payDetailId", payResult.getData().get("payDetailId"));
            }
        } catch (Exception e) {
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
         return JSONObject.toJSONString(result);
    }
    /**
     * @Author	huangguoqing 
     * @MethodName	commonPay 
     * @param params 支付请求参数
     * @return result
     * @Date	2015年2月28日
     * @Description:支付共通业务
     */
     private ResultMap commonPay(PMap params){
        ResultMap result = ResultMap.build();
        //支付确认业务处理
        ResultMap payResult = payManager.confirmPay(params);
        if(!Result.isSuccess(payResult)){
            return payResult;
        }
        /*调用支付网关*/
        //组装支付网关数据
        params.put("agencyCode",payResult.getData().get("agencyCode"));
        params.put("bankCode",payResult.getData().get("bankCode"));
        params.put("agencyMerchant",payResult.getData().get("agencyMerchant"));
        params.put("payTime",payResult.getData().get("payTime"));
        params.put("payDetailId",payResult.getData().get("payDetailId"));
        params.put("payFeeType",payResult.getData().get("payFeeType"));
        ResultMap result2 = payManager.getPayGateParams(params);
        if(!Result.isSuccess(result2)){
            return result2;
        }
        PMap payGateParams = (PMap)result2.getData().get("payGateMap");
        //调用支付网关
        logger.info("【支付请求】调用支付网关开始，参数为："+payGateParams);
        //ResultMap<String> payGateResult = payApi.preparePay(payGateParams);
         ResultMap<String> payGateResult = payPortal.preparePay(payGateParams);
        logger.info("【支付请求】调用支付网关结束,返回值为："+payGateResult.getData().get("returnUrl"));
        if(!Result.isSuccess(payGateResult)){
            return payGateResult;
        }
        String payUrl = (String)payGateResult.getData().get("returnUrl");
        result.withReturn(payUrl);
        result.addItem("payDetailId", payResult.getData().get("payDetailId"));
        return result;
    }
     
     /**
     * @Author	huangguoqing 
     * @MethodName	getWebChatCode 
     * @param content
     * @return 二维码显示字符串
     * @throws Exception 
     * @Date	2015年3月20日
     * @Description:根据URL产生二维码字符串
     */
    private String getWebChatCode(String content) throws Exception{
         BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 330, 330);
         BufferedImage
             qrcodeImg =
             MatrixToImageWriter.toBufferedImage(bitMatrix, new MatrixToImageConfig(0, 0xffffffff));
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ImageIO.write(qrcodeImg, "PNG", baos);
         baos.close();
         String base64 = Base64.encodeBase64String(baos.toByteArray());
         base64 = URLEncoder.encode(base64, "UTF-8");
         String wechatCode = String.format("data:image/png;base64,%s", base64);
         return wechatCode;
     }
    
    
    /**
     * @Author	huangguoqing 
     * @MethodName	doPayForWechat 
     * @param params
     * @param request
     * @return 微信二维码
     * @Date	2015年2月28日
     * @Description:商户微信扫码支付返回code
     */
    @RequestMapping({"/pay/doPayForWechat", "/api/pay/qrcode"})
    @ResponseBody
    public String doPayForWechat(PayParams params, HttpServletRequest request){
        ResultMap result = ResultMap.build();
        logger.info("【支付请求】进入doPayForWechat,请求参数为：" + JSONUtil.Bean2JSON(params));
        //将参数转化为map
        PMap paramMap = BeanUtil.Bean2PMap(params);
        //获得用户IP
        String ip = ServletUtil.getRealIp(request);
        paramMap.put("userIp", ip);
        paramMap.put("channelCode",params.getBankId());
        paramMap.put("channelType", "3");//支付渠道类型为3，即扫码支付
        /**1.验证签名**/
        Result signResult = secureManager.verifyAppSign(params);
        if(!Result.isSuccess(signResult)){
          //获取业务平台签名失败
          logger.error(signResult.getStatus().getMessage());
          return JSONObject.toJSONString(signResult);
        }
        logger.info("【支付请求】通过验证签名！");
        /**2.验证参数**/
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() != 0) {
            //验证参数失败，调到错误页面
            logger.error("【支付请求】" + validateResult.toString().substring(1,validateResult.toString().length()-1));
            result.withError(ResultStatus.PAY_PARAM_ERROR);
            return JSONObject.toJSONString(result);
        }
        //转义商品名称与描述
        paramMap = escapeSequence(paramMap);
        logger.info("【支付请求】通过验证参数！");
        /**3.生成支付单信息**/
        //查询该订单是否已存在
        ResultMap orderResult = payManager.selectPayOrderInfoByOrderId(params.getOrderId(),params.getAppId());
        if(!Result.isSuccess(orderResult)){
            result.withError(orderResult.getStatus());
            return JSONObject.toJSONString(result);
        }
        //插入支付单
        String payId = null;
        if(null != orderResult.getReturnValue()){
            payId = ((PayOrderInfo)orderResult.getReturnValue()).getPayId();
        } else {
            ResultMap payOrderResult = payManager.insertPayOrder(paramMap);
            if(!Result.isSuccess(payOrderResult)){
                //插入支付单失败
                return JSONObject.toJSONString(result.withError(ResultStatus.PAY_INSERT_PAY_ORDER_ERROR));
            }
            payId = payOrderResult.getReturnValue().toString();
        }
        logger.info("【支付请求】成功生成支付单信息！,支付单号为：" + payId);
        paramMap.put("payId", payId);
        ResultMap payResult = this.commonPay(paramMap);
        if(!Result.isSuccess(payResult)){
            //支付业务失败
            result.withError(ResultStatus.SYSTEM_ERROR);
            return JSONObject.toJSONString(result);
        }
        //向第三方支付机构发送支付请求
        String url = (String)payResult.getReturnValue();
        String qrCode = null;
        try {
            if(Constant.ALIPAY.equals(params.getBankId())){
                logger.info("【支付请求】生成的支付宝扫码值为：" + url);
                result.addItem("qrCode", url);
            } else if (Constant.WECHAT.equals(params.getBankId())){
                qrCode = getWebChatCode(url);
                logger.info("【支付请求】生成的微信扫码值为：" + qrCode);
                result.addItem("qrCode", qrCode);
            }
        } catch (Exception e) {
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return JSONObject.toJSONString(result);
    }
    
    private PMap escapeSequence(PMap pMap){
        pMap.put("productName", 
                StringUtils.trim((String)pMap.getString("productName").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")));
        if(!StringUtils.isEmpty((String)pMap.getString("productDesc"))){
            pMap.put("productDesc", 
                    StringUtils.trim((String)pMap.getString("productDesc").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")));
        }
        return pMap;
    }
    
    private boolean isExistRecommend(List<CommonAdaptModel> scanList){
        if(null == scanList || scanList.size() == 0)
            return false;
        for(CommonAdaptModel m : scanList){
            if (m.getChannelCode().equals(Constant.WECHAT)){
                return true;
            }
        }
        return false;
    }

    @RequestMapping("/pay/getSignData")
    @ResponseBody
    public String signData(@RequestParam Map<String, String> paramMap){
        //Map paramMap = convertToMap(params);
        Result<App> appresult = appManager.selectAppInfo(Integer.parseInt(paramMap.get("appId")));
        App app = appresult.getReturnValue();
        String key = app.getSignKey();
        String sign = DataSignUtil.sign(packParams(paramMap, key), "0");
        return JSONObject.toJSONString(sign);
    }
    
    private String packParams(Map paramMap, String secret) {
        if (paramMap == null) {
            return null;
        }
        List<String> keyList = new ArrayList<String>(paramMap.keySet());
        Collections.sort(keyList);

        //拼接k1=v1k2=v2
        StringBuilder paramStrBuilder = new StringBuilder();
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            Object value = paramMap.get(key);

            if (!StringUtils.isEmpty((String)value)) {
                paramStrBuilder.append(key).append("=").append(value.toString());
                if (i != keyList.size() - 1) {//拼接时，不包括最后一个&字符
                    paramStrBuilder.append("&");
                }
            }
        }

        //拼接secretKey
        paramStrBuilder.append(secret);
        return paramStrBuilder.toString();
        /*String encodeParam = "";
        try {
            encodeParam = URLEncoder.encode(paramStrBuilder.toString(), CommonConstant.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return encodeParam;*/
    }
    private Map convertToMap(Object params) {
        if (params instanceof Map) {
            return MapUtil.dropNulls((Map) params);
        } else {
            return BeanUtil.Bean2MapNotNull(params);
        }
    }
}
