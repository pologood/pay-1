package com.sogou.pay.wallet.service.entity;

import com.sogou.pay.wallet.enums.TranscStatus;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiepeidong on 2016/2/24.
 */
public class WalletTranscTopup {

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public TranscStatus getStatus() {
        return status;
    }

    public void setStatus(TranscStatus status) {
        this.status = status;
    }

    private int tid;
    private int uid;
    private BigDecimal money;
    private Date createTime;
    private String orderId;
    private String channelCode;
    private TranscStatus status;
}
