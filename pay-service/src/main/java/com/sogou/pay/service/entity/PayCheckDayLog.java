package com.sogou.pay.service.entity;

import java.util.Date;

/**
 * @Author qibaichao
 * @ClassName PayClearDayLogPo
 * @Date 2015年2月16日
 * @Description:对账操作日志实体
 */
public class PayCheckDayLog {
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
     * 状态
     */
    private int status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 对账日期
     */
    private String checkDate;
    /**
     * 支付平台
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
