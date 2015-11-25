package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Liwei
 * Date: 15/3/5
 * Time: 上午10:27
 * Description:响应流水实体
 */
public class PayResDetail implements Serializable {
    //支付流水单ID
    private String payDetailId;

    //接入平台 1：PC在线支付  2：移动支付
    private int accessPlatform;

    //付款方式 1:网银 2:第三方余额支付 3:扫码支付 4:SDK
    private int payFeeType;

    //使用余额
    private BigDecimal balance;

    //应付金额(支付单金额-使用余额)
    private BigDecimal trueMoney;

    //手续费
    private BigDecimal payFee;

    //费率
    private BigDecimal feeRate;
    
    //商户号
    private String merchantNo;

    //第三方支付编码
    private String agencyCode;

    // 第三方支付流水号
    private String agencyOrderId;

    // 银行简称
    private String bankCode;

    // 银行流水号
    private String bankOrderId;

    // 银行类型 1:储蓄卡 2:借记卡 3:支付机构 4:不区分
    private int bankCardType;

    // 平台支付时间
    private Date agencyPayTime;

    // 退款识别码
    private String refundCode;

    // 支付状态
    private int payStatus;

    // 创建时间
    private Date createTime;

    public String getPayDetailId() {
        return payDetailId;
    }

    public void setPayDetailId(String payDetailId) {
        this.payDetailId = payDetailId;
    }

    public int getAccessPlatform() {
        return accessPlatform;
    }

    public void setAccessPlatform(int accessPlatform) {
        this.accessPlatform = accessPlatform;
    }

    public int getPayFeeType() {
        return payFeeType;
    }

    public void setPayFeeType(int payFeeType) {
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

    public BigDecimal getPayFee() {
        return payFee;
    }

    public void setPayFee(BigDecimal payFee) {
        this.payFee = payFee;
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

    public String getAgencyOrderId() {
        return agencyOrderId;
    }

    public void setAgencyOrderId(String agencyOrderId) {
        this.agencyOrderId = agencyOrderId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankOrderId() {
        return bankOrderId;
    }

    public void setBankOrderId(String bankOrderId) {
        this.bankOrderId = bankOrderId;
    }

    public int getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(int bankCardType) {
        this.bankCardType = bankCardType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getAgencyPayTime() {
        return agencyPayTime;
    }

    public void setAgencyPayTime(Date agencyPayTime) {
        this.agencyPayTime = agencyPayTime;
    }

    public String getRefundCode() {
        return refundCode;
    }

    public void setRefundCode(String refundCode) {
        this.refundCode = refundCode;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    @Override
    public String toString() {
        return "PayResDetail{" +
                "payDetailId='" + payDetailId + '\'' +
                ", accessPlatform=" + accessPlatform +
                ", payFeeType=" + payFeeType +
                ", balance=" + balance +
                ", trueMoney=" + trueMoney +
                ", payFee=" + payFee +
                ", feeRate=" + feeRate +
                ", merchantNo='" + merchantNo + '\'' +
                ", agencyCode='" + agencyCode + '\'' +
                ", agencyOrderId='" + agencyOrderId + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankOrderId='" + bankOrderId + '\'' +
                ", bankCardType=" + bankCardType +
                ", agencyPayTime=" + agencyPayTime +
                ", refundCode='" + refundCode + '\'' +
                ", payStatus=" + payStatus +
                ", createTime=" + createTime +
                '}';
    }
}
