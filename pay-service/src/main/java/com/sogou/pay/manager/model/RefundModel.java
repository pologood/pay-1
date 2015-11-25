package com.sogou.pay.manager.model;

import java.math.BigDecimal;

/**
 * User: hujunfei
 * Date: 2015-03-02 18:25
 */
public class RefundModel {
    private int appId;
    private String orderId;
    private BigDecimal refundAmount;
    private String bgurl;
    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getBgurl() {
        return bgurl;
    }

    public void setBgurl(String bgurl) {
        this.bgurl = bgurl;
    }
}