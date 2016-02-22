package com.sogou.pay.web.controller.api;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sogou.pay.common.cache.RedisUtils;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.http.utils.MyThread;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultBean;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.model.ChannelAdaptModel;
import com.sogou.pay.manager.model.CommonAdaptModel;
import com.sogou.pay.manager.payment.AppManager;
import com.sogou.pay.manager.payment.ChannelAdaptManager;
import com.sogou.pay.manager.payment.OrderQueryManager;
import com.sogou.pay.manager.payment.PayManager;
import com.sogou.pay.manager.payment.PayTransferQueryManager;
import com.sogou.pay.manager.payment.RefundManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.service.config.PayConfig;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.entity.Channel;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.entity.PayBankRouter;
import com.sogou.pay.service.payment.AppService;
import com.sogou.pay.service.payment.ChannelService;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.service.payment.PayBankRouterService;
import com.sogou.pay.service.utils.DataSignUtil;
import com.sogou.pay.service.utils.email.EmailSender;
import com.sogou.pay.service.utils.orderNoGenerator.SequencerGenerator;
//import com.sogou.pay.thirdpay.api.PayApi;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.form.PayParams;
import com.sogou.pay.web.utils.ServletUtil;
/**
 * @Author	KpiController
 * @ClassName	KpiController
 * @Date	2015年2月28日
 * @Description: KpiController
 */
@Controller
@RequestMapping(value = "/kpi")
public class KpiController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(KpiController.class);

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
    private RedisUtils redisUtils;

    @Autowired
    private SequencerGenerator sequencerGenerator;

    @Autowired
    private PayTransferQueryManager payTransferQueryManager;

    @Autowired
    private OrderQueryManager orderQueryManager;

    @Autowired
    private RefundManager refundManager;
    
    @Autowired
    private EmailSender emailSender;
    
    @Autowired
    private ChannelService channelService;
    
    @Autowired
    private PayBankRouterService payBankRouterService;
    
    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;
    
    @Autowired
    private AppService appService;
    //支付渠道类型 1：银行 2：第三方机构 3：扫码支付
    private static final int CHANNEL_TYPE_BANK = 1;
    
    private static final int CHANNEL_TYPE_AGENCY = 2;
    
    private static final int CHANNEL_TYPE_SY = 3;
    /**
     * @Author	huangguoqing
     * @MethodName	doPay
     * @param request
     * @return ModelAndView
     * @Date	2015年10月10日
     * @Description: KPI收银台
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/kpi/showCashier",
            timeThreshold = 100, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("/showCashier")
    public ModelAndView showCashier(HttpServletRequest request){
        ModelAndView view = new ModelAndView("kpiSuccess");
        String ip = ServletUtil.getRealIp(request);
        //设置KPI参数
        MyThread showCashier=new MyThread(ip){
            Long start = System.currentTimeMillis();
            final String subject = "【KPI报警】收银台报警";
            public void run(){
                String ip = super.getIp();
                //获得用户IP
                PayParams params = getPayParams();
                params.setAccessPlatform("1");
                logger.info("【支付请求】进入showCashier,ip="+ip+",请求参数为：" + JSONUtil.Bean2JSON(params));
                //将参数转化为map
                PMap paramMap = BeanUtil.Bean2PMap(params);
                paramMap.put("userIp", ip);
                paramMap.put("channelCode",params.getBankId());
                //转义商品名称与描述
                paramMap = escapeSequence(paramMap);
                //查询该订单是否已经支付
                ResultMap orderResult = payManager.selectPayOrderInfoByOrderId(params.getOrderId(),params.getAppId());
                if(!Result.isSuccess(orderResult)){
                  //发邮件
                    sendEmail(subject, orderResult.getMessage());
                    return;
                }
                String payId = "ZFD20151019094230134001";
                logger.info("【支付请求】成功生成支付单信息！支付单号为：" + payId);
                /**进行银行适配**/
                //银行适配
                ResultBean<ChannelAdaptModel> resultBean = channelAdaptMaanger.getChannelAdapt(
                        Integer.parseInt(params.getAppId()), Integer.parseInt(params.getAccessPlatform()));
                if(!Result.isSuccess(resultBean)){
                    //获得支付渠道失败
                    sendEmail(subject, resultBean.getMessage());
                    return;
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
                if(commonPayList.isEmpty() && payOrgList.isEmpty() && scanCodeList.isEmpty() && b2bList.isEmpty()){
                    sendEmail(subject, ResultStatus.PAY_PARAM_ERROR.getMessage());
                    return;
                }
                //获得收款方信息
                Result<App> appResult = appManager.selectAppInfo(Integer.parseInt(params.getAppId()));
                if(!Result.isSuccess(appResult)){
                    sendEmail(subject, appResult.getStatus().getMessage());
                    return;
                }
                if(System.currentTimeMillis() - start > 4000){
                    sendEmail(subject, "系统超时！");
                }
                logger.info("【支付请求】返回收银台！ip=" + ip);
            }
         };
        showCashier.start();
        return view;
    }

    /**
     * @Author  huangguoqing
     * @MethodName  pcAlipay
     * @param request
     * @return ModelAndView
     * @Date    2015年10月10日
     * @Description: Pc端支付宝支付
     */
    @RequestMapping("/pcAlipay")
    public ModelAndView pcAlipay(HttpServletRequest request){
        ModelAndView view = new ModelAndView("kpiSuccess");
        //获得用户IP
        String ip = ServletUtil.getRealIp(request);
        //设置KPI参数
        MyThread pcAlipay=new MyThread(ip){
            Long start = System.currentTimeMillis();
            final String subject = "【KPI报警】支付宝PC报警";
            public void run(){
                String ip = super.getIp();
                PayParams params = getPayParams();
                params.setBankId("ALIPAY");
                params.setAccessPlatform("1");
                params.setSign(signData(params));
                logger.info("【支付请求】进入pcAlipay,ip="+ip+",请求参数为：" + JSONUtil.Bean2JSON(params));
                //将参数转化为map
                PMap paramMap = BeanUtil.Bean2PMap(params);
                paramMap.put("userIp", ip);
                paramMap.put("channelCode",params.getBankId());
                //转义商品名称与描述
                paramMap = escapeSequence(paramMap);
                /**生成支付单信息**/
                //查询该订单是否已经支付
                ResultMap orderResult = payManager.selectPayOrderInfoByOrderId(params.getOrderId(), params.getAppId());
                if(!Result.isSuccess(orderResult)){
                    //系统错误或者该支付单已经支付完成,跳到错误页面
                    sendEmail(subject, orderResult.getStatus().getMessage());
                    return;
                }
                String payId = "ZFD20151019094246226001";
                logger.info("【支付请求】成功生成支付单信息！支付单号为：" + payId);
                /**支付业务处理**/
                //将支付单ID放入map
                paramMap.put("payId",payId);
                ResultMap payResult = commonPay(paramMap);
                if(!Result.isSuccess(payResult)){
                    //支付业务失败,跳到错误页面
                    sendEmail(subject, payResult.getStatus().getMessage());
                    return;
                }
                if(System.currentTimeMillis() - start > 4000){
                    sendEmail(subject, "系统超时！");
                }
                logger.info("【支付请求】支付请求结束！ip=" + ip);
        }};
        pcAlipay.start();
        return view;
    }

    /**
     * @Author  huangguoqing
     * @MethodName  pcBank
     * @param request
     * @return ModelAndView
     * @Date    2015年10月10日
     * @Description: Pc端银行支付
     */
    @RequestMapping("/pcBank")
    public ModelAndView pcBank(HttpServletRequest request){
        ModelAndView view = new ModelAndView("kpiSuccess");
        //获得用户IP
        String ip = ServletUtil.getRealIp(request);
        //设置KPI参数
        MyThread pcBank=new MyThread(ip){
            Long start = System.currentTimeMillis();
            final String subject = "【KPI报警】网银报警";
            public void run(){
                String ip = super.getIp();
                //设置KPI参数
                PayParams params = getPayParams();
                params.setBankId("CMB");
                params.setAccessPlatform("1");
                params.setSign(signData(params));
                logger.info("【支付请求】进入pcBank,ip="+ip+",请求参数为：" + JSONUtil.Bean2JSON(params));
                //将参数转化为map
                PMap paramMap = BeanUtil.Bean2PMap(params);
                //获得用户IP
                paramMap.put("userIp", ip);
                paramMap.put("channelCode",params.getBankId());
                //转义商品名称与描述
                paramMap = escapeSequence(paramMap);
                /**生成支付单信息**/
                //查询该订单是否已经支付
                ResultMap orderResult = payManager.selectPayOrderInfoByOrderId(params.getOrderId(),params.getAppId());
                if(!Result.isSuccess(orderResult)){
                    //系统错误或者该支付单已经支付完成,跳到错误页面
                    sendEmail(subject, orderResult.getStatus().getMessage());
                    return;
                }
                String payId = "ZFD20151019094300410001";
                logger.info("【支付请求】成功生成支付单信息！支付单号为：" + payId);
                /**支付业务处理**/
                //将支付单ID放入map
                paramMap.put("payId",payId);
                ResultMap payResult = commonPay(paramMap);
                if(!Result.isSuccess(payResult)){
                    //支付业务失败,跳到错误页面
                    sendEmail(subject, payResult.getStatus().getMessage());
                    return;
                }
                if(System.currentTimeMillis() - start > 4000){
                    sendEmail(subject, "系统超时！");
                }
                logger.info("【支付请求】支付请求结束！ip="+ip);
        }};
        pcBank.start();
        return view;
    }

    /**
     * @Author  huangguoqing
     * @MethodName  wapPay
     * @param request
     * @return ModelAndView
     * @Date    2015年10月10日
     * @Description: Pc端支付
     */
    @RequestMapping("/wapPay")
    public ModelAndView wapPay(HttpServletRequest request){
        ModelAndView view = new ModelAndView("kpiSuccess");
      //获得用户IP
        String ip = ServletUtil.getRealIp(request);
        //设置KPI参数
        MyThread wapPay=new MyThread(ip){
            Long start = System.currentTimeMillis();
            final String subject = "【KPI报警】支付宝WAP报警";
            public void run(){
                String ip = super.getIp();
                //设置KPI参数
                PayParams params = getPayParams();
                params.setBankId("ALIPAY");
                params.setAccessPlatform("2");
                params.setSign(signData(params));
                logger.info("【支付请求】进入wapPay,请求参数为：ip="+ip+"," + JSONUtil.Bean2JSON(params));
                //将参数转化为map
                PMap<String,String> paramMap = BeanUtil.Bean2PMap(params);
                paramMap.put("userIp", ip);
                paramMap.put("channelCode",params.getBankId());
        
                if(StringUtils.isBlank(params.getBankId())){
                    //支付渠道为空
                    logger.error("【支付请求】支付渠道为空");
                    sendEmail(subject, ResultStatus.PAY_PARAM_ERROR.getMessage());
                    return;
                }
                //转义商品名称与描述
                paramMap = escapeSequence(paramMap);
                logger.info("【支付请求】通过验证参数！");
                /**3.生成支付单信息**/
                //查询该订单是否已经支付
                ResultMap orderResult = payManager.selectPayOrderInfoByOrderId(params.getOrderId(),params.getAppId());
                if(!Result.isSuccess(orderResult)){
                    logger.error("【支付请求】检查订单信息错误！selectPayOrderInfoByOrderId()..");
                    //系统错误或者该支付单已经支付完成,跳到错误页面
                    sendEmail(subject, orderResult.getStatus().getMessage());
                    return;
                }
                String payId = "ZFD20151019094310724001";
                logger.info("【支付请求】成功生成支付单信息！支付单号为：" + payId);
                /**4.支付业务处理**/
                //将支付单ID放入map
                paramMap.put("payId",payId);
                ResultMap payResult = commonPay(paramMap);
                if(!Result.isSuccess(payResult)){
                    //支付业务失败,跳到错误页面
                    sendEmail(subject, payResult.getStatus().getMessage());
                    return;
                }
                if(System.currentTimeMillis() - start > 4000){
                    sendEmail(subject, "系统超时！");
                }
                logger.info("【支付请求】支付请求结束！ip="+ip);
        }};
        wapPay.start();
        return view;
    }

    /**
     * @Author  huangguoqing
     * @MethodName  doPay
     * @param request
     * @return ModelAndView
     * @Date    2015年10月10日
     * @Description: KPI检查网络
     */
    @RequestMapping("/checkNet")
    public ModelAndView checkNet(HttpServletRequest request){
        ModelAndView view = new ModelAndView("kpiSuccess");
        final String subject = "【KPI报警】第三方支付机构接口";
        Thread alipayPC=new Thread(){
           public void run(){
               //支付宝支付、退款、查询接口
               String errorMsg = isConnect("https://mapi.alipay.com/gateway.do");
               if(!StringUtils.isEmpty(errorMsg)){
                   //发邮件
                   String content = "支付宝PC接口异常，请查看！" + errorMsg;
                   sendEmail(subject, content);
               }
           }
        };
        Thread alipayWAP=new Thread(){
            public void run(){
                //支付宝WAP接口
                String errorMsg = isConnect("http://wappaygw.alipay.com/service/rest.htm");
                if(!StringUtils.isEmpty(errorMsg)){
                    //发邮件
                    String content = "支付宝WAP接口异常，请查看！" + errorMsg;
                    sendEmail(subject, content);
                }
           }
        };
        Thread wechatPay=new Thread(){
            public void run(){
                //微信支付
                String errorMsg = isConnect("https://api.mch.weixin.qq.com/pay/unifiedorder");
                if(!StringUtils.isEmpty(errorMsg)){
                    //发邮件
                    String content = "微信支付接口异常，请查看！" + errorMsg;
                    sendEmail(subject, content);
                }
           }
        };
        Thread wechatQuery=new Thread(){
            public void run(){
                //微信查询接口
                String errorMsg = isConnect("https://api.mch.weixin.qq.com/pay/orderquery");
                if(!StringUtils.isEmpty(errorMsg)){
                    //发邮件
                    String content = "微信查询接口异常，请查看！" + errorMsg;
                    sendEmail(subject, content);
                }
           }
        };
        alipayPC.start();
        alipayWAP.start();
        wechatPay.start();
        wechatQuery.start();
        return view;
    }

    @RequestMapping("/checkPeak")
    public ModelAndView checkPeak(HttpServletRequest request){
        ModelAndView view = new ModelAndView("kpiSuccess");
        String errorMsg = isConnect("http://up.sohu.com/channel/gw/orderpay.up");
        if(!StringUtils.isEmpty(errorMsg)){
            view.setViewName("common/error");
        }
        return view;
    }
    /**
     * @Author  huangguoqing
     * @MethodName  payOk
     * @param request
     * @return ModelAndView
     * @Date    2015年10月10日
     * @Description: 测试
     */
    @RequestMapping("/payOk")
    public ModelAndView payOk(HttpServletRequest request){
        ModelAndView view = new ModelAndView("kpiSuccess");
        String ip = ServletUtil.getRealIp(request);
        logger.info("【KPI_OK】:IP="+ip);
        return view;
    }

    /**
     * @Author	huangguoqing
     * @MethodName	commonPay
     * @param params 支付请求参数
     * @return result
     * @Date	2015年2月28日
     * @Description:支付共通
     */     
    private ResultMap commonPay(PMap params){
        ResultMap result = ResultMap.build();
        //支付确认业务处理
        ResultMap payResult = this.confirmPay(params);
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
        ResultMap getPayGateResult = payManager.getPayGateParams(params);
        if(!Result.isSuccess(getPayGateResult)){
            return getPayGateResult;
        }
        return result;
    }

    /**
     * qibaichao 添加
     * 测试前置机
     * @param params
     * @param request
     * @return
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/kpi/frontMachine",
            timeThreshold = 100, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("frontMachine")
    public ModelAndView frontMachine(PayParams params, HttpServletRequest request){
        ModelAndView view = new ModelAndView("kpiSuccess");
        String resultXmlStr = HttpUtil.sendPost(PayConfig.payTranferHost, "");
        if(StringUtils.isEmpty(resultXmlStr)){
            //失败,跳到错误页面
            return setErrorPage("前置机异常",-2);
        }
        return view;
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

    private String signData(PayParams params){
        Map paramMap = convertToMap(params);
        Result<App> appresult = appManager.selectAppInfo(Integer.parseInt(params.getAppId()));
        App app = appresult.getReturnValue();
        String key = app.getSignKey();
        String sign = DataSignUtil.sign(packParams(paramMap, key), "0");
        return sign;
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

            if (value != null) {
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

    private PayParams getPayParams(){
        PayParams params = new PayParams();
        params.setVersion("v1.0");
        params.setOrderId(sequencerGenerator.getOrderNo());
        params.setOrderAmount("0.01");
        params.setOrderTime(new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()));
        params.setAccountId("KPI_ID");
        params.setProductName("KPI_NAME");
        params.setProductNum("1");
        params.setAppId("9999");
        params.setSignType("0");
        params.setPageUrl("http://www.pageUrl.kpi.com");
        params.setBgUrl("http://www.bgUrl.kpi.com");
        return params;
    }

    private String isConnect(String urlStr) {
        String errorSring = null;
        URL url;
        HttpURLConnection connection;
        int state = -1;
        try {
            url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            state = connection.getResponseCode();
            if (state != 200) {
                errorSring = "该地址探测错误，responseCode = " + state;
            }
        } catch (Exception ex) {
            errorSring = ex.getMessage();
        }
        return errorSring;
    }
    
    private ResultMap confirmPay(PMap params) {
        logger.info("【支付请求】进入confirmPay()，参数为："+params);
        ResultMap result = ResultMap.build();
        //业务参数校验
        ResultMap checkResult = checkParams(params);
        if (!Result.isSuccess(checkResult)) {
            //校验业务参数失败
            logger.error(checkResult.getStatus().getMessage());
            return checkResult;
        }
        /*判断渠道是否是第三方余额支付*/
        String channelType = params.getString("channelType");
        String channelCode = params.getString("channelCode");
        Channel channel = channelService.selectChannelByCode(params.getString("channelCode"));
        String agencyCode = null;
        String bankCode = null;
        String insertBankCode = null;
        //付款方式 1，网银 2，第三方  3，扫码支付
        int payFeeType = 0;
        //若是SDK，则支付机构类型为第三方支付
        if(Integer.parseInt(params.getString("accessPlatform")) == 3){
          //渠道为第三方机构，机构编码即为渠道编码
            agencyCode = channel.getChannelCode();
            //银行编码即为渠道编码
            insertBankCode = agencyCode;
            payFeeType = 2;
        } else {
            if(null == channelType){
                if (CHANNEL_TYPE_BANK == channel.getChannelType()) {
                    //渠道为银行支付,路由出第三方支付机构
                    agencyCode = getBankRouter(params);
                    if (null == agencyCode) {
                        //路由第三方支付机构失败
                        result.withError(ResultStatus.PAY_BANK_ROUTER_NOT_EXIST);
                        return result;
                    }
                    insertBankCode = channel.getChannelCode();
                    bankCode = insertBankCode;
                    payFeeType = 1;
                } else if(CHANNEL_TYPE_AGENCY == channel.getChannelType()){
                    //渠道为第三方机构，机构编码即为渠道编码
                    agencyCode = channel.getChannelCode();
                    //银行编码即为渠道编码
                    insertBankCode = agencyCode;
                    payFeeType = 2;
                } else {
                  //渠道为扫码支付，机构编码即为渠道编码
                    agencyCode = channel.getChannelCode();
                    //银行编码即为渠道编码
                    insertBankCode = agencyCode;
                    payFeeType = 3;
                }
            } else {
                int channelTypeId = Integer.parseInt(channelType);
                if (CHANNEL_TYPE_BANK == channelTypeId) {
                    //渠道为银行支付,路由出第三方支付机构
                    agencyCode = getBankRouter(params);
                    if (null == agencyCode) {
                        //路由第三方支付机构失败
                        result.withError(ResultStatus.PAY_BANK_ROUTER_NOT_EXIST);
                        return result;
                    }
                    insertBankCode = channel.getChannelCode();
                    bankCode = insertBankCode;
                    payFeeType = 1;
                } else if(CHANNEL_TYPE_AGENCY == channelTypeId){
                    //渠道为第三方机构，机构编码即为渠道编码
                    agencyCode = channel.getChannelCode();
                    //银行编码即为渠道编码
                    insertBankCode = agencyCode;
                    payFeeType = 2;
                } else if(CHANNEL_TYPE_SY == channelTypeId){
                  //渠道为扫码支付，机构编码即为渠道编码
                    agencyCode = channel.getChannelCode();
                    //银行编码即为渠道编码
                    insertBankCode = agencyCode;
                    payFeeType = 3;
                } else {
                    //B2B支付
                    agencyCode = getBankRouter(params);
                    if (null == agencyCode) {
                        //路由第三方支付机构失败
                        result.withError(ResultStatus.PAY_BANK_ROUTER_NOT_EXIST);
                        return result;
                    }
                    insertBankCode = channel.getChannelCode();
                    bankCode = insertBankCode;
                    payFeeType = 4;
                }
            }
        }
        /*获取机构商户信息*/
        //获得业务平台所属公司信息
        App app = appService.selectApp(params.getInt("appId"));
        if (null == app) {
            //业务平台不存在
            logger.error(ResultStatus.PAY_APP_NOT_EXIST.getMessage());
            result.withError(ResultStatus.PAY_APP_NOT_EXIST);
            return result;
        }
        PayAgencyMerchant agencyMerchant = new PayAgencyMerchant();
        agencyMerchant.setAgencyCode(agencyCode);
        agencyMerchant.setCompanyCode(app.getBelongCompany());
        agencyMerchant.setAppId(params.getInt("appId"));
        PayAgencyMerchant merchant = payAgencyMerchantService.selectPayAgencyMerchant(agencyMerchant);
        if(null == merchant){
            //支付机构商户不存在
            logger.error(ResultStatus.PAY_MERCHANT_NOT_EXIST.getMessage());
            result.withError(ResultStatus.PAY_MERCHANT_NOT_EXIST);
            return result;
        }
        /*生成支付流水单、支付单与支付流水对应信息*/
        //生成支付流水单
        String payDetailId = sequencerGenerator.getPayDetailId();
        Date payTime = new Date();
        //支付成功将银行信息、支付机构、支付机构商户信息、支付流水号、支付时间回传
        result.addItem("agencyCode", agencyCode);
        result.addItem("bankCode", bankCode);
        result.addItem("agencyMerchant", merchant);
        result.addItem("payDetailId", payDetailId);
        result.addItem("payFeeType", payFeeType);
        result.addItem("payTime", payTime);
        return result;
    }
    
    public ResultMap checkParams(PMap params) {
        ResultMap result = ResultMap.build();
        //校验金额
        BigDecimal orderAmount = new BigDecimal(params.getString("orderAmount"));
        if (orderAmount.compareTo(orderAmount.ZERO) < 0) {
            //金额出错
            result.withError(ResultStatus.PAY_PARAM_ERROR);
            return result;
        }
        //校验支付渠道
        String channelCode = params.getString("channelCode");
        Channel channel = channelService.selectChannelByCode(channelCode);
        if (null == channel) {
            //渠道信息不存在
            result.withError(ResultStatus.PAY_CHANNEL_NOT_EXIST);
            return result;
        }
        return result;
    }
    
    private String getBankRouter(PMap params) {
        //获得路由规则
        PayBankRouter payBankRouter = new PayBankRouter();
        payBankRouter.setBankCode(params.getString("channelCode"));
        if (!StringUtils.isEmpty(params.getString("bankCardType"))){
            payBankRouter.setBankCardType(Integer.parseInt(params.getString("bankCardType")));
        }
        payBankRouter.setAppId(Integer.parseInt(params.getString("appId")));
        payBankRouter.setRouterStatus(1);//已启用
        //查询第三方机构路由信息
        List<PayBankRouter> routerList = payBankRouterService.selectPayBankRouterList(payBankRouter);
        if (null == routerList) {
            //该渠道未能适配出第三方支付机构
            logger.error(ResultStatus.PAY_BANK_ROUTER_NOT_EXIST.getMessage());
            return null;
        }
        //根据路由信息路由出一个支付机构
        return routerAgency(routerList);
    }

    private String routerAgency(List<PayBankRouter> routerList) {
        //产生随机数(1到10000)
        int random = (int) (Math.random() * 10000) + 1;
        logger.info("路由随机数为：" + random);
        int routerListSize = routerList.size();
        int totalSacle = 0;
        for (int i = 0; i < routerListSize; i++) {
            totalSacle += routerList.get(i).getScale() * 10000;
            if (random <= totalSacle) {
                logger.info("路由支付机构为:" + routerList.get(i).getAgencyCode());
                return routerList.get(i).getAgencyCode();
            }
        }
        //不会走到以下语句
        return null;
    }
    
    private void sendEmail(String subject,String content){
        emailSender.sendEmail("error.ftl", subject, content,
                "gaopenghui@sogou-inc.com", "xiepeidong@sogou-inc.com");
    }
}
