package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author huangguoqing
 * @ClassName Channel
 * @Date 2015年2月28日
 * @Description:渠道实体信息
 */
public class Channel implements Serializable{
    //自增ID
    private Integer id;

    //渠道编码
    private String channelCode;

    //渠道性质
    private Integer channelNature;

    //渠道名称
    private String channelName;

    //logo图片地址
    private String logo;

    //最低限额
    private BigDecimal lowLimit;

    //最高限额
    private BigDecimal highLimit;

    //页面显示信息
    private String limitInfo;

    //支付渠道类型
    private Integer channelType;

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

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Integer getChannelNature() {
        return channelNature;
    }

    public void setChannelNature(Integer channelNature) {
        this.channelNature = channelNature;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
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

    public String getLimitInfo() {
        return limitInfo;
    }

    public void setLimitInfo(String limitInfo) {
        this.limitInfo = limitInfo;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
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
