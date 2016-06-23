/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.thirdpay.biz.enums;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年6月17日;
//-------------------------------------------------------
public enum UnionpaySubTxnType {

  DEFAULT("00"),//默认
  SELF_SERVICE_CONSUMPTION("01"),//自助消费 通过地址的方式区分前台消费和后台消费(含无跳转支付)
  INSTALMENT("03");//分期付款
  
  private String value;

  private UnionpaySubTxnType(String value) {
    this.value = value;
  }
  
  public String getValue(){
    return value;
  }

}
