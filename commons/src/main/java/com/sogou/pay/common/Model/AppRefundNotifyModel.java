package com.sogou.pay.common.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by qibaichao on 2015/4/9.
 * 退款通知对象
 */
public class AppRefundNotifyModel implements Serializable {


    private String appId;
    //通知地址
    private String notifyUrl;

    //接入平台订单号
    private String orderId;

    //支付单号
    private String payId;

    //TRADE_FINISHED
    private String refundStatus;

    //支付金额
    private String payAmount;

    //交易金额
    private String refundAmount;

    //退款完成时间
    private String refundSuccessTime;

    //签名
    private String sign;

    //签名类型
    private String signType;

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getRefundSuccessTime() {
        return refundSuccessTime;
    }

    public void setRefundSuccessTime(String refundSuccessTime) {
        this.refundSuccessTime = refundSuccessTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
