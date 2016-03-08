package com.sogou.pay.web.controller.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.thirdpay.api.PayPortal;
import org.apache.commons.lang.StringUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sogou.pay.common.cache.RedisUtils;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.payment.AppManager;
import com.sogou.pay.manager.payment.ChannelAdaptManager;
import com.sogou.pay.manager.payment.PayManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.service.entity.PayOrderInfo;
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
//@RequestMapping(value = "/paywap")
@SuppressWarnings("all")
public class PayWapController extends BaseController{
    
    private static final Logger logger = LoggerFactory.getLogger(PayWapController.class);
    
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
    @Profiled(el = true, logger = "webTimingLogger", tag = "/paywap/doPay",
            timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    @RequestMapping({"/paywap/doPay", "/gw/pay/wap"})
    public ModelAndView doPay(PayParams params, HttpServletRequest request,HttpServletResponse response){
        ModelAndView view = new ModelAndView("wapForward");
        logger.info("【支付请求】进入dopay,请求参数为：" + JSONUtil.Bean2JSON(params));
        //将参数转化为map
        PMap<String,String> paramMap = BeanUtil.Bean2PMap(params);
        //获得用户IP
        String ip = ServletUtil.getRealIp(request);
        paramMap.put("userIp", ip);
        paramMap.put("channelCode",params.getBankId());
        /**2.验证参数**/
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() != 0) {
            //验证参数失败，调到错误页面
            logger.error("【支付请求】" + validateResult.toString().substring(1,validateResult.toString().length()-1));
            return setErrorPage(ResultStatus.PAY_PARAM_ERROR, "wap");
        }
        if(StringUtils.isBlank(params.getBankId())){
            //支付渠道为空
            logger.error("【支付请求】支付渠道为空");
            return setErrorPage(ResultStatus.PAY_PARAM_ERROR, "wap");
        }
        /**1.验证签名**/
        Result signResult = secureManager.verifyAppSign(params);
        if(!Result.isSuccess(signResult)){
            logger.error("【支付请求】验证签名错误！");
          //获取业务平台签名失败,跳到错误页面
          return setErrorPage(signResult.getStatus(), "wap");
        }
        logger.info("【支付请求】通过验证签名！");

        //转义商品名称与描述
        paramMap = escapeSequence(paramMap);
        logger.info("【支付请求】通过验证参数！");
        /**3.生成支付单信息**/
        //查询该订单是否已经支付
        ResultMap orderResult = payManager.selectPayOrderInfoByOrderId(params.getOrderId(),params.getAppId());
        if(!Result.isSuccess(orderResult)){
            logger.error("【支付请求】检查订单信息错误！selectPayOrderInfoByOrderId()..");
            //系统错误或者该支付单已经支付完成,跳到错误页面
            return setErrorPage(orderResult.getStatus(), "wap");
        }
        String payId = null;
        if(null != orderResult.getReturnValue()){
            payId = ((PayOrderInfo)orderResult.getReturnValue()).getPayId();
        } else {
            ResultMap payOrderResult = payManager.insertPayOrder(paramMap);
            if(!Result.isSuccess(payOrderResult)){
                //插入支付单失败,跳到错误页面
                return setErrorPage(payOrderResult.getStatus(), "wap");
            }
            payId = payOrderResult.getReturnValue().toString();
        }
        logger.info("【支付请求】成功生成支付单信息！支付单号为：" + payId);
        /**4.支付业务处理**/
        //将支付单ID放入map
        paramMap.put("payId",payId);
        ResultMap payResult = this.commonPay(paramMap);
        if(!Result.isSuccess(payResult)){
            //支付业务失败,跳到错误页面
            return setErrorPage(payResult.getStatus(), "wap");
        }
        view.addObject("payUrl", payResult.getReturnValue());
        logger.info("【支付请求】支付请求结束！");
        return view;
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
        ResultMap getPayGateResult = payManager.getPayGateParams(params);
        if(!Result.isSuccess(getPayGateResult)){
            return getPayGateResult;
        }
        PMap payGateMap = (PMap)getPayGateResult.getData().get("payGateMap");
        //调用支付网关
        logger.info("【支付请求】调用支付网关开始，参数为："+payGateMap);
        //ResultMap<String> payGateResult = payApi.preparePay(payGateMap);
         ResultMap<String> payGateResult = payPortal.preparePay(payGateMap);
        logger.info("【支付请求】调用支付网关结束,返回值为："+payGateResult.getData().get("returnUrl"));
        if(!Result.isSuccess(payGateResult)){
            return payGateResult;
        }
        result.withReturn(payGateResult.getData().get("returnUrl"));
        return result;
    }
     
    private PMap<String,String> escapeSequence(PMap<String,String> pMap){
        pMap.put("productName", 
                StringUtils.trim((String)pMap.getString("productName").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")));
        if(!StringUtils.isEmpty((String)pMap.getString("productDesc"))){
            pMap.put("productDesc", 
                    StringUtils.trim((String)pMap.getString("productDesc").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")));
        }
        return pMap;
    }
}
