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
  trade_inquiry("00"), //交易查询
  consumption("01"), //消费
  pre_authorization("02"), //预授权
  pre_authorization_completion("03"), //预授权完成
  refund("04"), //退货
  credit_transfer("05"), //圈存
  collection("11"), //代收
  payment("12"), //代付
  bill_payment("13"), //账单支付
  transfer("14"), //转账(保留)
  batch_trade("21"), //批量交易
  batch_inquiry("22"), //批量查询
  consumption_withdrawal("31"), //消费撤销
  pre_authorization_withdrawal("32"), //预授权撤销
  pre_authorization_completion_withdrawal("33"), //预授权完成撤销
  balance_inquiry("71"), //余额查询
  card_binding("72"), //实名认证-建立绑定关系
  bill_inquiry("73"), //账单查询
  binding_dissolution("74"), //解除绑定关系
  binding_inquiry("75"), //查询绑定关系
  verification_code_sending("77"), //发送短信验证码交易
  trade_inquiry_open("78"), //开通查询交易
  trade_open("79"), //开通交易
  IC_card_script_notice("94");//IC卡脚本通知

  private String value;

  private UnionpayTxnType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
