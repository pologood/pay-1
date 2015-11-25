package com.sogou.pay.thirdpay.api.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.thirdpay.api.PayApi;
import com.sogou.pay.thirdpay.biz.AliPayService;
import com.sogou.pay.thirdpay.biz.BillPayService;
import com.sogou.pay.thirdpay.biz.TenPayService;
import com.sogou.pay.thirdpay.biz.WechatPayService;
import com.sogou.pay.thirdpay.biz.utils.Utils;

import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:08
 */
@Component
public class PayApiImpl implements PayApi {

    private static final Logger log = LoggerFactory.getLogger(PayApiImpl.class);
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private TenPayService tenPayService;
    @Autowired
    private WechatPayService wechatPayService;
    @Autowired
    private BillPayService billPayService;

    @Override
    @Profiled(el = true, logger = "httpClientTimingLogger", tag = "PayApiImpl_preparePay",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    public ResultMap<String> preparePay(PMap params) {
        log.info("Payment Gateway Start!Parameters:" + JsonUtil.beanToJson(params));
        ResultMap result = ResultMap.build();
        //1.验证参数合法性
        boolean isEmptyParams = verifyParams(params);
        if (!isEmptyParams) {
            log.error("Payment Gateway:Lack Parameter Or Parameter Illegal,Parameters:" + JsonUtil.beanToJson(params));
            return result.build(ResultStatus.THIRD_PAY_PARAM_ERROR);
        }
        //2.根据不同支付机构请求不接口获取支付参数
        String payChannle = params.getString("payChannle");
        try {
            switch (payChannle) {
                //1.1支付宝账户支付
                case "PC_ALIPAY_ACCOUNT":
                    result = aliPayService.AccountPreparePayInfo(params);
                    break;
                //1.2.支付宝网关支付
                case "PC_ALIPAY_GATEWAY":
                    result = aliPayService.GatwayPreparePayInfo(params);
                    break;
                //1.3.支付宝扫码支付
                case "PC_ALIPAY_SWEEPYARD":
                    result = aliPayService.SweepYardsPreparePayInfo(params);
                    break;
                //1.4.支付宝客户端支付
                case "MOBILE_ALIPAY_CLIENT":
                    result = aliPayService.ClientPreparePayInfo(params);
                    break;
                //1.5.支付宝WAP支付
                case "MOBILE_ALIPAY_WAP":
                    result = aliPayService.WapPreparePayInfo(params);
                    break;
                //2.1.财付通账户支付
                case "PC_TENPAY_ACCOUNT":
                    result = tenPayService.AccountPreparePayInfo(params);
                    break;
                //2.2.财付通网关支付
                case "PC_TENPAY_GATEWAY":
                    result = tenPayService.GatwayYPreparePayInfo(params);
                    break;
                //2.3.财付通客户端支付
                case "MOBILE_TENPAY_CLIENT":
                    result = ResultMap.build();
                    break;
                //3.1.微信扫码支付
                case "PC_WECHAT_SWEEPYARD":
                    result = wechatPayService.SweepYardsPreparePayInfo(params);
                    break;
                //3.2.微信客户端支付
                case "MOBILE_WECHAT_CLIENT":
                    result = wechatPayService.ClientPreparePayInfo(params);
                    break;
                //3.2.快钱支付（包括B2C和B2C）
                case "PC_99BILL":
                    result = billPayService.preparePayInfo(params);
                    break;
                default:
                    log.error("Payment Gateway:Does Not Support Pay Channel，Parameters:" + JsonUtil.beanToJson(params));
                    result.build(ResultStatus.THIRD_PAY_CHANNEL_NOT_EXIST);
            }
        } catch (ServiceException se) {
            log.error("Payment Gateway:Get Pay Parameters Unusually，ServiceException", se.toString() + "Parameters:" + JsonUtil.beanToJson(params));
            return result.build(se.getStatus());
        } catch (Exception e) {
            log.error("Payment Gateway:Get Pay Parameters Unusually，Exception", e.toString() + "Parameters:" + JsonUtil.beanToJson(params));
            return result.build(ResultStatus.THIRD_PAY_SYSTEM_ERROR);
        }
        log.info("Payment Gateway End!Parameters:" + JsonUtil.beanToJson(params) + "Return Result:" + JsonUtil.beanToJson(result));
        return result;
    }

    /**
     * 验证参数合法性
     */
    public boolean verifyParams(PMap params) {
        boolean isEmpty = false;
        //1.验证共同参数是否为空
        if (Utils.isEmpty(params.getString("payChannle"), params.getString("merchantNo"),
                params.getString("serverNotifyUrl"),
                params.getString("serialNumber"), params.getString("subject"),
                params.getString("buyerIp"), params.getString("payTime"),
                params.getString("orderAmount"), params.getString("md5securityKey"))) {
            return isEmpty;
        }
        //2.验证金额是否大于0
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        int compareAmount = oAmount.compareTo(new BigDecimal(0));
        if (compareAmount != 1) {
            return isEmpty;
        }
        String payChannle = params.getString("payChannle");
        switch (payChannle) {
            //1.1支付宝账户支付
            case "PC_ALIPAY_ACCOUNT":
                //1.3.支付宝扫码支付
            case "PC_ALIPAY_SWEEPYARD":
                //2.1.财付通账户支付
            case "PC_TENPAY_ACCOUNT":
                //3.1.微信扫码支付
            case "PC_WECHAT_SWEEPYARD":
                if (Utils.isEmpty(params.getString("payUrl"), params.getString("pageNotifyUrl"))) {
                    return isEmpty;
                } else {
                    isEmpty = true;
                }
                break;
            //1.2.支付宝网关支付
            case "PC_ALIPAY_GATEWAY":
                //2.2.财付通网关支付
            case "PC_TENPAY_GATEWAY":
                if (Utils.isEmpty(params.getString("bankCode"), params.getString("payUrl"), params.getString("pageNotifyUrl"))) {
                    return isEmpty;
                } else {
                    isEmpty = true;
                }
                break;
            //1.4.支付宝客户端支付
            case "MOBILE_ALIPAY_CLIENT":
                if (Utils.isEmpty(params.getString("prepayUrl"), params.getString("payUrl"))) {
                    return isEmpty;
                } else {
                    isEmpty = true;
                }
                break;
            //1.5.支付宝WAP支付
            case "MOBILE_ALIPAY_WAP":
                if (Utils.isEmpty(params.getString("prepayUrl"), params.getString("payUrl"), params.getString("pageNotifyUrl"))) {
                    return isEmpty;
                } else {
                    isEmpty = true;
                }
                break;
            //2.3.财付通客户端支付
            case "MOBILE_TENPAY_CLIENT":
                //3.2.微信客户端支付
            case "MOBILE_WECHAT_CLIENT":
                if (Utils.isEmpty(params.getString("publicCertFilePath"),
                        params.getString("privateCertFilePath"), params.getString("pageNotifyUrl"))) {
                    return isEmpty;
                } else {
                    isEmpty = true;
                }
                break;
            case "PC_99BILL":
                isEmpty = true;
                break;
            default:
                isEmpty = false;
        }
        return isEmpty;
    }
}
