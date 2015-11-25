package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wujingpan on 2015/3/5.
 */
public class PayChannelAdapt implements Serializable {
    private Integer id;
    //业务ID
    private Integer appId;
    //接入平台 1：PC; 2：移动
    private Integer accessPlatform;
    //渠道简称
    private String channelCode;
    //渠道类型 1，网银 2，第三方  3，扫码支付
    private Integer channelType;
    //银行卡类型 1，储蓄卡；2，信用卡；3，不区分
    private Integer bankCardType;
    //状态 1：可用；2：不可用。
    private Integer status;
    //排序
    private Integer sort;
    //创建时间
    private Date createTime;
    //修改时间
    private Date modifyTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getAccessPlatform() {
        return accessPlatform;
    }

    public void setAccessPlatform(Integer accessPlatform) {
        this.accessPlatform = accessPlatform;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public Integer getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(Integer bankCardType) {
        this.bankCardType = bankCardType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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

    @Override
    public String toString() {
        return "PayChannelAdapt{" +
                "id=" + id +
                ", appId=" + appId +
                ", accessPlatform=" + accessPlatform +
                ", channelCode='" + channelCode + '\'' +
                ", channelType=" + channelType +
                ", bankCardType=" + bankCardType +
                ", status=" + status +
                ", sort=" + sort +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
