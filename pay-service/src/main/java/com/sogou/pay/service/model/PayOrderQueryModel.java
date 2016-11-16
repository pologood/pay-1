package com.sogou.pay.service.model;

import com.sogou.pay.service.entity.App;

import java.math.BigDecimal;

/**
 * Date: 2015-03-02 18:25
 */
public class PayOrderQueryModel {
  private App app;
  private String orderId;
  private String payId;
  private BigDecimal refundAmount;
  private String bgurl;
  private boolean fromCashier = false;

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

  public String getPayId() {
    return payId;
  }

  public void setPayId(String payId) {
    this.payId = payId;
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

  public boolean isFromCashier() {
    return fromCashier;
  }

  public void setFromCashier(boolean fromCashier) {
    this.fromCashier = fromCashier;
  }
}