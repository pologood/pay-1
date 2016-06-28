package com.sogou.pay.service.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author qibaichao
 * @ClassName PayCheckWaitingPo
 * @Date 2015年2月16日
 * @Description:平台待对账单实体
 */
public class PayCheckWaiting {
    /**
     * id
     */
    private long id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date modifyTime;
    /**
     * 版本
     */
    private Short version;
    /**
     * 我方流水号
     */
    private String instructId;
    /**
     * 对方流水号
     */
    private String outOrderId;
    /**
     * 业务代码
     */
    private int checkType;
    /**
     * 交易时间
     */
    private Date outTransTime;
    /**
     * 金额
     */
    private BigDecimal bizAmt;
    /**
     * 费率
     */
    private BigDecimal feeRate;
    /**
     * 手续费
     */
    private BigDecimal commissionFeeAmt;
    /**
     * 状态
     */
    private int status;
    /**
     * 接入平台
     */
    private int accessPlatform;
    /**
     * 业务平台ID
     */
    private int appId;
    /**
     * 付款方式
     */
    private int payType;
    /**
     * 银行编码
     */
    private String bankCode;
    /**
     * 对账日期
     */
    private String checkDate;
    /**
     * 机构编码
     */
    private String agencyCode;
    /**
     * 商户号
     */
    private String merchantNo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
    }

    public String getInstructId() {
        return instructId;
    }

    public void setInstructId(String instructId) {
        this.instructId = instructId;
    }

    public String getOutOrderId() {
        return outOrderId;
    }

    public void setOutOrderId(String outOrderId) {
        this.outOrderId = outOrderId;
    }

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }


    public Date getOutTransTime() {
        return outTransTime;
    }

    public void setOutTransTime(Date outTransTime) {
        this.outTransTime = outTransTime;
    }

    public BigDecimal getBizAmt() {
        return bizAmt;
    }

    public void setBizAmt(BigDecimal bizAmt) {
        this.bizAmt = bizAmt;
    }

    public BigDecimal getCommissionFeeAmt() {
        return commissionFeeAmt;
    }

    public void setCommissionFeeAmt(BigDecimal commissionFeeAmt) {
        this.commissionFeeAmt = commissionFeeAmt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAccessPlatform() {
        return accessPlatform;
    }

    public void setAccessPlatform(int accessPlatform) {
        this.accessPlatform = accessPlatform;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
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

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }
}
