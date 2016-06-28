package com.sogou.pay.service.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by qibaichao on 2015/3/20.
 * 手续费对账结果实体
 */
public class PayCheckFeeResult {
    /**
     * 主键
     */
    private Long id;
    /**
     * 入库时间
     */
    private Date createTime;
    /**
     * 我方总笔数
     */
    private int totalNum;
    /**
     * 我方总数续费金额
     */
    private BigDecimal totalFee;
    /**
     * 对方总笔数
     */
    private int outTotalNum;
    /**
     * 对方总手续费金额
     */
    private BigDecimal outTotalFee;
    /**
     * 1：支付 3：退款
     */
    private int checkType;
    /**
     * 1：对账成功
     * 2：金额不等
     */
    private int status;
    /**
     * 对账日期
     */
    private String checkDate;
    /**
     * 机构编码
     */
    private String agencyCode;

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

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public int getOutTotalNum() {
        return outTotalNum;
    }

    public void setOutTotalNum(int outTotalNum) {
        this.outTotalNum = outTotalNum;
    }

    public BigDecimal getOutTotalFee() {
        return outTotalFee;
    }

    public void setOutTotalFee(BigDecimal outTotalFee) {
        this.outTotalFee = outTotalFee;
    }

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
}
