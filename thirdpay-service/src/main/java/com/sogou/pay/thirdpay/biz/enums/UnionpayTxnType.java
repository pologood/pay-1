/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.thirdpay.biz.enums;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年6月15日;
//-------------------------------------------------------
public enum UnionpayTxnType {

  //中国银联交易类型代码
  TRADE_INQUIRY("00"), //交易查询
  CONSUMPTION("01"), //消费
  PRE_AUTHORIZATION("02"), //预授权
  PRE_AUTHORIZATION_COMPLETION("03"), //预授权完成
  REFUND("04"), //退货
  CREDIT_TRANSFER("05"), //圈存
  COLLECTION("11"), //代收
  PAYMENT("12"), //代付
  BILL_PAYMENT("13"), //账单支付
  TRANSFER("14"), //转账(保留)
  BATCH_TRADE("21"), //批量交易
  BATCH_INQUIRY("22"), //批量查询
  CONSUMPTION_WITHDRAWAL("31"), //消费撤销
  PRE_AUTHORIZATION_WITHDRAWAL("32"), //预授权撤销
  PRE_AUTHORIZATION_COMPLETION_WITHDRAWAL("33"), //预授权完成撤销
  BALANCE_INQUIRY("71"), //余额查询
  CARD_BINDING("72"), //实名认证-建立绑定关系
  BILL_INQUIRY("73"), //账单查询
  BINDING_DISSOLUTION("74"), //解除绑定关系
  BINDING_INQUIRY("75"), //查询绑定关系
  VERIFICATION_CODE_SENDING("77"), //发送短信验证码交易
  TRADE_INQUIRY_OPEN("78"), //开通查询交易
  TRADE_OPEN("79"), //开通交易
  IC_CARD_SCRIPT_NOTICE("94");//IC卡脚本通知

  private String value;

  private UnionpayTxnType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
