package com.sogou.pay.manager.model;

import java.io.Serializable;

/**
 * Created by wujingpan on 2015/3/6.
 */
public class CommonAdaptModel implements Serializable {

    /**
     * 银行编码
     */
    public String channelCode;

    /**
     * 银行名称
     */
    public String bankName;

    /**
     * 银行logo
     */
    public String logo;

    /**
     * 渠道方式 1：网银在线支付；2第三方支付  3：扫码支付
     */
    public Integer channelType;

    /**
     * 银行限额最低
     */
    public String lowLimit;

    /**
     * 银行限额最高
     */
    public String highLimit;

    /**
     * 页面显示信息
     */
    public String limitInfo;

    /**
     * 页面排序
     */
    public Integer sort;

    /**
     * 银行卡类型
     */
    public Integer bankCardType;

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Integer getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(Integer bankCardType) {
        this.bankCardType = bankCardType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }

    public String getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(String highLimit) {
        this.highLimit = highLimit;
    }

    public String getLimitInfo() {
        return limitInfo;
    }

    public void setLimitInfo(String limitInfo) {
        this.limitInfo = limitInfo;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getBankCodeType() {
        return bankCardType;
    }

    public void setBankCodeType(Integer bankCodeType) {
        this.bankCardType = bankCodeType;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    @Override
    public String toString() {
        return "CommonAdaptModel{" +
                "channelCode='" + channelCode + '\'' +
                ", bankName='" + bankName + '\'' +
                ", logo='" + logo + '\'' +
                ", channelType=" + channelType +
                ", lowLimit='" + lowLimit + '\'' +
                ", highLimit='" + highLimit + '\'' +
                ", limitInfo='" + limitInfo + '\'' +
                ", sort=" + sort +
                ", bankCardType=" + bankCardType +
                '}';
    }
}
