package com.sogou.pay.manager.model.thirdpay;

/**
 * 支付请求组装参数
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/20 19:04
 */
public class FairAccRefundModel {
    private int appId;
    private String orderId;
    private String payId;
    private String payDetailId;

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

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getPayDetailId() {
        return payDetailId;
    }

    public void setPayDetailId(String payDetailId) {
        this.payDetailId = payDetailId;
    }
}