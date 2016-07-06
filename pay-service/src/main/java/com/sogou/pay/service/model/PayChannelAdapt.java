package com.sogou.pay.service.model;


import java.math.BigDecimal;

public class PayChannelAdapt {

    private String channelCode;

    private String channelName;

    private Integer channelNature;

    private Integer channelType;

    private Integer accessPlatform;

    private Integer bankCardType;

    private String logo;

    private BigDecimal lowLimit;

    private BigDecimal highLimit;

    private Integer sort;

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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
