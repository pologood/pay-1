package com.sogou.pay.service.entity;

import java.util.Date;

/**
 * @Author huangguoqing
 * @Date 2015/3/3 14:35
 * @Description: 渠道适配规则信息
 */
public class PayChannelRule {
    //规则ID
    private Integer ruleId;

    //业务平台ID
    private Integer appId;

    //接入平台
    private Integer accessPlatfrom;

    //渠道编码
    private String channelCode;

    //渠道类型(用于页面展示)
    private Integer channelType;

    //银行卡类型：1：储蓄卡 2：信用卡 3：不区分
    private Integer bankCardType;

    //创建时间
    private Date createTime;

    //修改时间
    private Date modifyTime;

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getAccessPlatfrom() {
        return accessPlatfrom;
    }

    public void setAccessPlatfrom(Integer accessPlatfrom) {
        this.accessPlatfrom = accessPlatfrom;
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
