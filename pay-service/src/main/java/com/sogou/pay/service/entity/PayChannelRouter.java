package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 渠道路由
 */
public class PayChannelRouter implements Serializable{
    //自增ID
    private Integer id;
    //渠道id
    private Integer channelId;
    //商户id
    private Integer merchantId;
    //路由权重
    private Double weight;
    //状态，1：启用 2：禁用
    private Integer status;
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

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
