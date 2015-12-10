package com.sogou.pay.web.controller.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.cache.RedisUtils;
import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultBean;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.common.utils.PMapUtil;
import com.sogou.pay.manager.payment.AppManager;
import com.sogou.pay.manager.payment.ChannelAdaptManager;
import com.sogou.pay.manager.payment.PayManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.utils.Constant;
import com.sogou.pay.service.utils.orderNoGenerator.SequencerGenerator;
import com.sogou.pay.thirdpay.api.PayApi;
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
@RequestMapping(value = "/paysdk")
@SuppressWarnings("all")
public class PaySDKController extends BaseController{
    
    private static final Logger logger = LoggerFactory.getLogger(PaySDKController.class);
    
    @Autowired
    private PayManager payManager;

    @Autowired
    private ChannelAdaptManager channelAdaptMaanger;
    
    @Autowired
    private AppManager appManager;
    
    @Autowired
    private SecureManager secureManager;
    
    @Autowired
    private PayApi payApi;
    
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SequencerGenerator sequencerGenerator;

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
    @Profiled(el = true, logger = "webTimingLogger", tag = "/paysdk/doPay",
            timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("/doPay")
    @ResponseBody
    public String doPay(PayParams params, HttpServletRequest request,HttpServletResponse response){
        ResultBean resultBean = ResultBean.build();
        logger.info("【支付请求】进入dopay,请求参数为：" + JsonUtil.beanToJson(params));
        //将参数转化为map
        PMap<String,String> paramMap = PMapUtil.fromBean(params);
        //获得用户IP
        String ip = ServletUtil.getRealIp(request);
        paramMap.put("userIp", ip);
        paramMap.put("channelCode",params.getBankId());
        /**1.验证签名**/
        Result signResult = secureManager.verifyAppSign(params);
        if(!Result.isSuccess(signResult)){
            logger.error("【支付请求】验证签名错误！");
          //获取业务平台签名失败
          return String.valueOf(signResult.getStatus().getCode());
        }
        logger.info("【支付请求】通过验证签名！");
        /**2.验证参数**/
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() != 0) {
            //验证参数失败
            logger.error("【支付请求】" + validateResult.toString().substring(1,validateResult.toString().length()-1));
            return String.valueOf(ResultStatus.PARAM_ERROR.getCode());
        }
        //转义商品名称与描述
        paramMap = escapeSequence(paramMap);
        logger.info("【支付请求】通过验证参数！");
        /**3.生成支付单信息**/
        //查询该订单是否已经支付
        ResultMap orderResult = payManager.selectPayOrderInfoByOrderId(params.getOrderId(),params.getAppId());
        if(!Result.isSuccess(orderResult)){
            logger.error("【支付请求】检查订单信息错误！selectPayOrderInfoByOrderId()..");
            //系统错误或者该支付单已经支付完成
            return String.valueOf(orderResult.getStatus().getCode());
        }
        String payId = null;
        if(null != orderResult.getReturnValue()){
            payId = ((PayOrderInfo)orderResult.getReturnValue()).getPayId();
        } else {
            ResultMap payOrderResult = payManager.insertPayOrder(paramMap);
            if(!Result.isSuccess(payOrderResult)){
                //插入支付单失败
                return String.valueOf(payOrderResult.getStatus().getCode());
            }
            payId = payOrderResult.getReturnValue().toString();
        }
        logger.info("【支付请求】成功生成支付单信息！支付单号为：" + payId);
        /**4.判断是否有渠道支付信息，若无，则进行银行适配**/
        if(StringUtils.isBlank(params.getBankId())){
            //支付渠道为空
            logger.error("【支付请求】支付渠道为空");
            return String.valueOf(ResultStatus.PAY_BANKID_IS_NULL.getCode());
        } 
        /**5.支付业务处理**/
        //将支付单ID放入map
        paramMap.put("payId",payId);
        ResultMap payResult = this.commonPay(paramMap);
        if(!Result.isSuccess(payResult)){
            //支付业务失败
            return String.valueOf(payResult.getStatus().getCode());
        }
        return JSONObject.toJSONString(payResult);
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
        ResultMap<String> payGateResult = payApi.preparePay(payGateMap);
        logger.info("【支付请求】调用支付网关结束,返回值为：payGateResult:"+ payGateResult.getData().toString());
        if(!Result.isSuccess(payGateResult)){
            result.withError(ResultStatus.THIRD_PAY_ERROR);
            return result;
        }
        if(Constant.ALIPAY.equals(payResult.getData().get("agencyCode"))) {
            result.addItem("orderInfo", payGateResult.getData().get("strOrderInfo"));
            result.addItem("aliPublicKey", payGateResult.getData().get("aliPublicKey"));
        } else {
            result.addItem("returnData", payGateResult.getData().get("returnData"));
        }
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
