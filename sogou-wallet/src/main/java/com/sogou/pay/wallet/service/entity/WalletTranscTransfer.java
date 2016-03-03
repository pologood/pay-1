package com.sogou.pay.wallet.service.entity;

import com.sogou.pay.wallet.enums.TranscStatus;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiepeidong on 2016/2/24.
 */
public class WalletTranscTransfer {


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

    public int getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(int payeeId) {
        this.payeeId = payeeId;
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
    private int payeeId;
    private TranscStatus status;
}
