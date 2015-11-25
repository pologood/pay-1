package com.sogou.pay.thirdpay.api.impl;


import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.thirdpay.api.RefundApi;
import com.sogou.pay.thirdpay.biz.AliPayService;
import com.sogou.pay.thirdpay.biz.BillPayService;
import com.sogou.pay.thirdpay.biz.TenPayService;
import com.sogou.pay.thirdpay.biz.WechatPayService;
import com.sogou.pay.thirdpay.biz.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:18
 */
@Component
public class RefundApiImpl implements RefundApi {

    private static final Logger log = LoggerFactory.getLogger(RefundApiImpl.class);
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private TenPayService tenPayService;
    @Autowired
    private WechatPayService wechatPayService;
    @Autowired
    private BillPayService billPayService;

    @Override
    public ResultMap<String> refundOrder(PMap params) {
        log.info("Refund Order Start!Parameters:" + JsonUtil.beanToJson(params));
        ResultMap result = ResultMap.build();
        //1.验证参数合法性
        boolean isEmptyParams = verifyParams(params);
        if (!isEmptyParams) {
            log.error("Refund Order:Lack Parameter Or Parameter Illegal,Parameters:" + JsonUtil.beanToJson(params));
            return result.build(ResultStatus.THIRD_REFUND_PARAM_ERROR);
        }
        //2.根据不同支付机构调用退款接口
        String payChannle = params.getString("agencyCode");
        try {
            switch (payChannle) {
                //1支付宝
                case "ALIPAY":
                    result = aliPayService.refundOrderInfo(params);
                    break;
                //2.财付通
                case "TENPAY":
                    result = tenPayService.refundOrderInfo(params);
                    break;
                //3.微信
                case "WECHAT":
                    result = wechatPayService.refundOrderInfo(params);
                    break;
                //4.快钱
                case "BILL99":
                    result = billPayService.refundOrderInfo(params);
                    break;
                default:
                    log.error("Refund Order:Does Not Support Channel，Parameters:" + JsonUtil.beanToJson(params));
                    return result.build(ResultStatus.THIRD_REFUND_PARAM_ERROR);
            }
        } catch (ServiceException se) {
            log.error("Refund Order:Get Refund Order Unusually:", se + "Parameters:" + JsonUtil.beanToJson(params));
            result.build(se.getStatus());
        } catch (Exception e) {
            log.error("Refund Order:Get Refund Order Unusually:", e + "Parameters:" + JsonUtil.beanToJson(params));
            return result.build(ResultStatus.THIRD_REFUND_SYSTEM_ERROR);
        }
        log.info("Refund Order End!Parameters:" + JsonUtil.beanToJson(params) + "Return Result:" + JsonUtil.beanToJson(result));
        return result;
    }

    /**
     * 验证参数合法性
     */
    public boolean verifyParams(PMap params) {
        boolean isEmpty = false;
        //1.验证共同参数是否为空
        if (Utils.isEmpty(params.getString("agencyCode"), params.getString("merchantNo"),
                params.getString("refundUrl"), params.getString("refundNotifyUrl"),
                params.getString("md5securityKey"),
                params.getString("publicCertFilePath"),
                params.getString("privateCertFilePath"),
                params.getString("refundSerialNumber"), params.getString("refundReqTime"),
                params.getString("serialNumber"), params.getString("refundAmount"),
                params.getString("totalAmount"))) {
            return isEmpty;
        }
        //2.验证金额是否大于0
        BigDecimal refundAmount = new BigDecimal(params.getString("refundAmount"));
        BigDecimal totalAmount = new BigDecimal(params.getString("totalAmount"));
        int compareRefundAmount = refundAmount.compareTo(new BigDecimal(0));
        int compareTotalAmount = totalAmount.compareTo(new BigDecimal(0));
        if (compareRefundAmount != 1 || compareTotalAmount != 1) {
            return isEmpty;
        }
        return true;
    }
}