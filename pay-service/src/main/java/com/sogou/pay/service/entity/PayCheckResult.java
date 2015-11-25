package com.sogou.pay.service.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author qibaichao
 * @ClassName PayCheckResultPo
 * @Date 2015年2月16日
 * @Description:对账结果实体
 */
public class PayCheckResult {
    /**
     * id
     */
    private long id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 总数
     */
    private int totalNum;
    /**
     * 总金额
     */
    private BigDecimal totalAmt;
    /**
     * 对方总笔数
     */
    private int outTotalNum;
    /**
     * 外部总金额
     */
    private BigDecimal outTotalAmt;
    /**
     * 业务编码
     */
    private int bizCode;
    /**
     * 状态
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

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public int getOutTotalNum() {
        return outTotalNum;
    }

    public void setOutTotalNum(int outTotalNum) {
        this.outTotalNum = outTotalNum;
    }

    public BigDecimal getOutTotalAmt() {
        return outTotalAmt;
    }

    public void setOutTotalAmt(BigDecimal outTotalAmt) {
        this.outTotalAmt = outTotalAmt;
    }

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
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
