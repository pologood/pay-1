package com.sogou.pay.service.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by qibaichao on 2015/3/23.
 */
public class PayCheckFeeDiff {

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
     * 版本号
     */
    private int version;
    /**
     * 我方流水号
     */
    private String instructId;
    /**
     * 我方金额
     */
    private BigDecimal bizAmt;
    /**
     * 我方手续费
     */
    private BigDecimal feeAmt;
    /**
     * 对方流水号
     */
    private String outOrderId;
    /**
     * 对方金额
     */
    private BigDecimal outBizAmt;
    /**
     * 对方手续费
     */
    private BigDecimal outFeeAmt;
    /**
     * 业务代码
     */
    private int checkType;
    /**
     * 交易时间
     */
    private Date outTransTime;

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
    /**
     * 处理状态
     */
    private int handleStatus;
    /**
     * 备注
     */
    private String remark;

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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getInstructId() {
        return instructId;
    }

    public void setInstructId(String instructId) {
        this.instructId = instructId;
    }

    public BigDecimal getBizAmt() {
        return bizAmt;
    }

    public void setBizAmt(BigDecimal bizAmt) {
        this.bizAmt = bizAmt;
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public String getOutOrderId() {
        return outOrderId;
    }

    public void setOutOrderId(String outOrderId) {
        this.outOrderId = outOrderId;
    }

    public BigDecimal getOutBizAmt() {
        return outBizAmt;
    }

    public void setOutBizAmt(BigDecimal outBizAmt) {
        this.outBizAmt = outBizAmt;
    }

    public BigDecimal getOutFeeAmt() {
        return outFeeAmt;
    }

    public void setOutFeeAmt(BigDecimal outFeeAmt) {
        this.outFeeAmt = outFeeAmt;
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

    public int getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(int handleStatus) {
        this.handleStatus = handleStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
