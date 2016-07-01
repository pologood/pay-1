package com.sogou.pay.thirdpay.biz.enums;
/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年6月17日;
//-------------------------------------------------------
public enum UnionpayBizType {
  DEFAULT("000000"), //默认
  STOCK_FUND("000101"), //基金业务之股票基金
  MONETARY_FUND("000102"), //基金业务之货币基金
  B2C_GATEWAY_PAYMENT("000201"), //B2C网关支付
  AUTHENTICATION_PAYMENT("000301"), //认证支付2.0
  GRADE_PAYMENT("000302"), //评级支付
  PAYMENT("000401"), //代付
  COLLECTION("000501"), //代收
  BILL_PAYMENT("000601"), //账单支付
  INTERBANK_PAYMENT("000801"), //跨行支付
  APPLEPAY_PAYMENT("000802"), //ApplePay
  BINDING_PAYMENT("000901"), //绑定支付
  ORDER("001001"), //订购
  B2B("000202"); //B2B

  private String value;

  private UnionpayBizType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
