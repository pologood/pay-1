/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.common.Model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sogou.pay.common.utils.LocalDateTimeJsonSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年6月28日;
//-------------------------------------------------------
public class StdPayRequest {

  public enum PayType {
    PC_ACCOUNT(0), //账户支付
    PC_GATEWAY(1), //网关(网银)支付
    QRCODE(2), //扫码支付
    MOBILE_SDK(3), //SDK支付
    MOBILE_WAP(4);//WAP支付

    private int value;

    private PayType(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  /*channel*/
  private String agencyCode;//支付服务提供方代码:如支付宝-alipay

  private PayType payType;//支付方式代码:如扫码支付

  private String bankCode;//银行代码:如招商银行CMB

  private String merchantId;//支付服务提供方商户号

  private String payee;//收款账号

  /*order*/
  private String payId;//支付流水号

  private BigDecimal orderAmount;//订单金额

  @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
  private LocalDateTime payTime;//支付发起时间

  /*product*/
  private String productName;//商品名称

  /*payer*/
  private String accountId;

  private String payerIp;

  /*key*/
  private String md5Key;

  private String publicCertPath;

  private String privateCertPath;

  private String publicCertKey;

  private String privateCertKey;

  /*url*/
  private String payUrl;

  private String prepayUrl;//for wechat

  private String pageNotifyUrl;

  private String serverNotifyUrl;

  public String getAgencyCode() {
    return agencyCode;
  }

  public void setAgencyCode(String agencyCode) {
    this.agencyCode = agencyCode;
  }

  public PayType getPayType() {
    return payType;
  }

  public void setPayType(PayType payType) {
    this.payType = payType;
  }

  public String getBankCode() {
    return bankCode;
  }

  public void setBankCode(String bankCode) {
    this.bankCode = bankCode;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(String merchantId) {
    this.merchantId = merchantId;
  }

  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }

  public String getPayId() {
    return payId;
  }

  public void setPayId(String payId) {
    this.payId = payId;
  }

  public BigDecimal getOrderAmount() {
    return orderAmount;
  }

  public void setOrderAmount(BigDecimal orderAmount) {
    this.orderAmount = orderAmount;
  }

  public LocalDateTime getPayTime() {
    return payTime;
  }

  public void setPayTime(LocalDateTime payTime) {
    this.payTime = payTime;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getPayerIp() {
    return payerIp;
  }

  public void setPayerIp(String payerIp) {
    this.payerIp = payerIp;
  }

  public String getMd5Key() {
    return md5Key;
  }

  public void setMd5Key(String md5Key) {
    this.md5Key = md5Key;
  }

  public String getPublicCertPath() {
    return publicCertPath;
  }

  public void setPublicCertPath(String publicCertPath) {
    this.publicCertPath = publicCertPath;
  }

  public String getPrivateCertPath() {
    return privateCertPath;
  }

  public void setPrivateCertPath(String privateCertPath) {
    this.privateCertPath = privateCertPath;
  }

  public String getPublicCertKey() {
    return publicCertKey;
  }

  public void setPublicCertKey(String publicCertKey) {
    this.publicCertKey = publicCertKey;
  }

  public String getPrivateCertKey() {
    return privateCertKey;
  }

  public void setPrivateCertKey(String privateCertKey) {
    this.privateCertKey = privateCertKey;
  }

  public String getPayUrl() {
    return payUrl;
  }

  public void setPayUrl(String payUrl) {
    this.payUrl = payUrl;
  }

  public String getPrepayUrl() {
    return prepayUrl;
  }

  public void setPrepayUrl(String prepayUrl) {
    this.prepayUrl = prepayUrl;
  }

  public String getPageNotifyUrl() {
    return pageNotifyUrl;
  }

  public void setPageNotifyUrl(String pageNotifyUrl) {
    this.pageNotifyUrl = pageNotifyUrl;
  }

  public String getServerNotifyUrl() {
    return serverNotifyUrl;
  }

  public void setServerNotifyUrl(String serverNotifyUrl) {
    this.serverNotifyUrl = serverNotifyUrl;
  }

  public String getPayTimeString() {
    return payTime.format(FORMATTER);
  }

  public String getOrderAmountString() {
    return orderAmount.toString();
  }

}
