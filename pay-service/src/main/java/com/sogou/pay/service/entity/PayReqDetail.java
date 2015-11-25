package com.sogou.pay.service.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author huangguoqing
 * @Date 2015/3/3 14:06
 * @Description: 支付请求流水实体
 */
public class PayReqDetail {
    //支付流水单ID
    private String payDetailId;

    //接入平台 1：PC在线支付  2：移动支付
    private Integer accessPlatform;

    //付款方式 1:网银 2:第三方余额支付 3:扫码支付 4:SDK
    private Integer payFeeType;

    //使用余额
    private BigDecimal balance;

    //应付金额(支付单金额-使用余额)
    private BigDecimal trueMoney;

    //商户号
    private String merchantNo;

    //第三方支付编码
    private String agencyCode;

    //银行简称
    private String bankCode;

    //银行类型 1:储蓄卡 2:借记卡 3:支付机构
    private Integer bankCardType;

    //创建时间
    private Date createTime;

    public String getPayDetailId() {
        return payDetailId;
    }

    public void setPayDetailId(String payDetailId) {
        this.payDetailId = payDetailId;
    }

    public Integer getAccessPlatform() {
        return accessPlatform;
    }

    public void setAccessPlatform(Integer accessPlatform) {
        this.accessPlatform = accessPlatform;
    }

    public Integer getPayFeeType() {
        return payFeeType;
    }

    public void setPayFeeType(Integer payFeeType) {
        this.payFeeType = payFeeType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getTrueMoney() {
        return trueMoney;
    }

    public void setTrueMoney(BigDecimal trueMoney) {
        this.trueMoney = trueMoney;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public Integer getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(Integer bankCardType) {
        this.bankCardType = bankCardType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
