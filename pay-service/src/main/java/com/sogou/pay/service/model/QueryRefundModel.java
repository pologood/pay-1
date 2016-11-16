package com.sogou.pay.service.model;

import com.sogou.pay.service.entity.App;


public class QueryRefundModel {
    private App app;           // 应用ID
    private String orderId;         // 商户订单ID
    private String sign;            // 签名
    private String signType;        // 签名方式

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
}
