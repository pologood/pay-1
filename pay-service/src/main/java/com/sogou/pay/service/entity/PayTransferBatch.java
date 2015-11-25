package com.sogou.pay.service.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by qibaichao on 2015/6/1.
 * 代付单批次
 */
public class PayTransferBatch {

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
     * 审核人id
     */
    private int userId;
    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 交易状态
     */
    private int tradeState;
    /**
     * 审批意见
     */
    private String auditDesc;
    /**
     * 结果说明
     */
    private String resultDesc;
    /**
     * 流程实例号
     */
    private String reqNbr;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 转出帐号
     */
    private String dbtAcc;
    /**
     * 分行代码
     */
    private String bbkNbr;
    /**
     * 业务类别
     */
    private String busCod;
    /**
     * 业务模式编号
     */
    private String busMod;
    /**
     * 交易代码
     */
    private String trsTyp;
    /**
     * 计划笔数
     */
    private int planTotal;
    /**
     * 成功笔数
     */
    private int sucTotal;
    /**
     * 计划金额
     */
    private BigDecimal planAmt;
    /**
     * 成功金额
     */
    private BigDecimal sucAmt;
    /**
     * 用途
     */
    private String memo;
    /**
     * 0:未通知
     * 1:已通知,
     */
    private int notifyFlag;
    /**
     * 招行-业务参考号
     */
    private String yurref;

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTradeState() {
        return tradeState;
    }

    public void setTradeState(int tradeState) {
        this.tradeState = tradeState;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public String getReqNbr() {
        return reqNbr;
    }

    public void setReqNbr(String reqNbr) {
        this.reqNbr = reqNbr;
    }

    public String getBbkNbr() {
        return bbkNbr;
    }

    public void setBbkNbr(String bbkNbr) {
        this.bbkNbr = bbkNbr;
    }

    public String getBusCod() {
        return busCod;
    }

    public void setBusCod(String busCod) {
        this.busCod = busCod;
    }

    public String getBusMod() {
        return busMod;
    }

    public void setBusMod(String busMod) {
        this.busMod = busMod;
    }

    public String getTrsTyp() {
        return trsTyp;
    }

    public void setTrsTyp(String trsTyp) {
        this.trsTyp = trsTyp;
    }

    public int getPlanTotal() {
        return planTotal;
    }

    public void setPlanTotal(int planTotal) {
        this.planTotal = planTotal;
    }

    public int getSucTotal() {
        return sucTotal;
    }

    public void setSucTotal(int sucTotal) {
        this.sucTotal = sucTotal;
    }

    public BigDecimal getPlanAmt() {
        return planAmt;
    }

    public void setPlanAmt(BigDecimal planAmt) {
        this.planAmt = planAmt;
    }

    public BigDecimal getSucAmt() {
        return sucAmt;
    }

    public void setSucAmt(BigDecimal sucAmt) {
        this.sucAmt = sucAmt;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDbtAcc() {
        return dbtAcc;
    }

    public void setDbtAcc(String dbtAcc) {
        this.dbtAcc = dbtAcc;
    }

    public int getNotifyFlag() {
        return notifyFlag;
    }

    public void setNotifyFlag(int notifyFlag) {
        this.notifyFlag = notifyFlag;
    }

    public String getAuditDesc() {
        return auditDesc;
    }

    public void setAuditDesc(String auditDesc) {
        this.auditDesc = auditDesc;
    }

    public String getYurref() {
        return yurref;
    }

    public void setYurref(String yurref) {
        this.yurref = yurref;
    }
}
