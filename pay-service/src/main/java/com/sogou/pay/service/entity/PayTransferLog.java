package com.sogou.pay.service.entity;

import java.util.Date;

/**
 * Created by qibaichao on 2015/6/1.
 * 代付任务日志
 */
public class PayTransferLog {

    /**
     * 自增id
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 代付状态
     */
    private int status;
    /**
     * 单笔序列号
     */
    private String serial;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 备注
     */
    private String remark;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
