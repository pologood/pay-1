package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付渠道
 */
public class PayChannel implements Serializable{
    //自增ID
    private Integer channelId;

    //渠道编码
    private String channelCode;

    //渠道名称
    private String channelName;

    //渠道性质：1：支付；2：提现；3 : 支付和提现
    private Integer channelNature;

    //支付渠道类型：1：网银；2：支付机构；3：B2B网银
    private Integer channelType;

    //接入平台类型：1：PC；2：WAP；3：SDK；4：扫码
    private Integer accessPlatform;

    //支持的银行卡类型：1：储蓄卡；2：信用卡；3：不区分
    private Integer bankCardType;

    //状态，1：启用 2：禁用
    private Integer status;

    //排序顺序
    private Integer sort;

    //渠道logo图片地址
    private String logo;

    //在线支付的最低限额
    private BigDecimal lowLimit;

    //在线支付的最高限额
    private BigDecimal highLimit;

    //创建时间
    private Date createTime;

    //修改时间
    private Date modifyTime;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getChannelNature() {
        return channelNature;
    }

    public void setChannelNature(Integer channelNature) {
        this.channelNature = channelNature;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public Integer getAccessPlatform() {
        return accessPlatform;
    }

    public void setAccessPlatform(Integer accessPlatform) {
        this.accessPlatform = accessPlatform;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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
