package com.sogou.pay.service.model;

import com.sogou.pay.service.entity.App;

import java.math.BigDecimal;

/**
 * User: hujunfei
 * Date: 2015-03-02 18:25
 */
public class RefundModel {
  private App app;
  private String orderId;
  private BigDecimal refundAmount;
  private String bgurl;
  private int refundStatus;

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

  public int getRefundStatus() {
    return refundStatus;
  }

  public void setRefundStatus(int refundStatus) {
    this.refundStatus = refundStatus;
  }
}