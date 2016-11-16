package com.sogou.pay.service.entity;

import java.util.Date;

/**
 * 支付机构商户信息
 */
public class PayAgencyMerchant {

  //ID
  private Integer merchantId;
  //所属公司编码 (1:搜狗网络 2:搜狗科技)
  private Integer companyId;
  //业务平台ID
  private Integer appId;
  //加密方式 (1：签名；2：非对称加密 3：对账加密)
  private Integer encryptionType;
  //状态，1：启用 2：禁用
  private Integer status;
  //路由权重
  private Double weight;
  //支付机构编码
  private String agencyCode;
  //商户号
  private String merchantNo;
  //第三方开设的收款账号对应（邮箱）或者为微信公众号
  private String sellerEmail;
  //加密密钥
  private String encryptKey;
  //第三方公钥证书路径
  private String pubKeypath;
  //本地私钥证书路径
  private String privateKeypath;
  //创建时间
  private Date createTime;
  //修改时间
  private Date modifyTime;

  public Integer getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(Integer merchantId) {
    this.merchantId = merchantId;
  }

  public Integer getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Integer companyId) {
    this.companyId = companyId;
  }

  public Integer getAppId() {
    return appId;
  }

  public void setAppId(Integer appId) {
    this.appId = appId;
  }

  public Integer getEncryptionType() {
    return encryptionType;
  }

  public void setEncryptionType(Integer encryptionType) {
    this.encryptionType = encryptionType;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Double getWeight() {
    return weight;
  }

  public void setWeight(Double weight) {
    this.weight = weight;
  }

  public String getAgencyCode() {
    return agencyCode;
  }

  public void setAgencyCode(String agencyCode) {
    this.agencyCode = agencyCode;
  }

  public String getMerchantNo() {
    return merchantNo;
  }

  public void setMerchantNo(String merchantNo) {
    this.merchantNo = merchantNo;
  }

  public String getSellerEmail() {
    return sellerEmail;
  }

  public void setSellerEmail(String sellerEmail) {
    this.sellerEmail = sellerEmail;
  }

  public String getEncryptKey() {
    return encryptKey;
  }

  public void setEncryptKey(String encryptKey) {
    this.encryptKey = encryptKey;
  }

  public String getPubKeypath() {
    return pubKeypath;
  }

  public void setPubKeypath(String pubKeypath) {
    this.pubKeypath = pubKeypath;
  }

  public String getPrivateKeypath() {
    return privateKeypath;
  }

  public void setPrivateKeypath(String privateKeypath) {
    this.privateKeypath = privateKeypath;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Date modifyTime) {
    this.modifyTime = modifyTime;
  }
}
