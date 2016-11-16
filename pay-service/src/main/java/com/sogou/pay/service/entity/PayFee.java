package com.sogou.pay.service.entity;

import java.math.BigDecimal;
import java.util.Date;

public class PayFee {
    public static final int FEETYPE_FEERATE=1;
    public static final int FEETYPE_FEE = 2;

    //费率id
    private String id;
    //费率名称
    private String feeName;
    //第三方支付机构编码
    private String agencyCode;
    //第三方支付机构商户号
    private String merchantNo;
    //接入平台
    private Integer accessPlatform;
    //付款方式
    private Integer payFeeType;
    //费率类型
    private Integer feeType;
    //固定手续费，单位：分
    private BigDecimal fee;
    //手续费率，费率类型为按比例
    private BigDecimal feeRate;
    //手续费保底，单位：分，-1表示下不保底；定额费率此项无效
    private BigDecimal lowLimit;
    //手续费封顶，单位：分，-1表示上不封顶；定额费率此项无效
    private BigDecimal highLimit;
    //启用状态0：未启用；1：已启用
    private Integer status;
    //创建时间
    private Date createTime;
    //修改时间
    private Date modifyTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
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

    public Integer getFeeType() {
        return feeType;
    }

    public void setFeeType(Integer feeType) {
        this.feeType = feeType;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public BigDecimal getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(BigDecimal lowLimit) {
        this.lowLimit = lowLimit;
    }

    public BigDecimal getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(BigDecimal highLimit) {
        this.highLimit = highLimit;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
