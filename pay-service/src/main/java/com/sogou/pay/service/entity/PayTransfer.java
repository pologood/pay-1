package com.sogou.pay.service.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by qibaichao on 2015/6/1.
 * 代付单
 */
public class PayTransfer {

    /**
     * 自增id
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date modifyTime;
    /**
     * 业务平台ID
     */
    private int appId;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 外部关联号
     */
    private String outRef;
    /**
     * 付款状态
     */
    private int payStatus;
    /**
     * 收款方银行帐号
     */
    private String recBankAcc;
    /**
     * 收款方真实姓名
     */
    private String recName;
    /**
     * 付款金额
     */
    private BigDecimal payAmt;
    /**
     * 手续费
     */
    private BigDecimal fee;
    /**
     * 付款说明
     */
    private String payDesc;
    /**
     * 系统内标志
     */
    private String bankFlag;
    /**
     * 他行户口开户行
     */
    private String otherBank;
    /**
     * 他行户口开户地
     */
    private String otherCity;
    /**
     * 结果描述
     */
    private String resultDesc;
    /**
     * 单笔序列号
     */
    private String serialNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getOutRef() {
        return outRef;
    }

    public void setOutRef(String outRef) {
        this.outRef = outRef;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public String getRecBankAcc() {
        return recBankAcc;
    }

    public void setRecBankAcc(String recBankAcc) {
        this.recBankAcc = recBankAcc;
    }

    public String getRecName() {
        return recName;
    }

    public void setRecName(String recName) {
        this.recName = recName;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public String getPayDesc() {
        return payDesc;
    }

    public void setPayDesc(String payDesc) {
        this.payDesc = payDesc;
    }

    public String getBankFlag() {
        return bankFlag;
    }

    public void setBankFlag(String bankFlag) {
        this.bankFlag = bankFlag;
    }

    public String getOtherBank() {
        return otherBank;
    }

    public void setOtherBank(String otherBank) {
        this.otherBank = otherBank;
    }

    public String getOtherCity() {
        return otherCity;
    }

    public void setOtherCity(String otherCity) {
        this.otherCity = otherCity;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
}
