package com.sogou.pay.wallet.service.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiepeidong on 2016/2/24.
 */
public class WalletTranscLucky {

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

    public int getLuckyId() {
        return luckyId;
    }

    public void setLuckyId(int luckyId) {
        this.luckyId = luckyId;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    private int tid;
    private int uid;
    private BigDecimal money;
    private Date createTime;
    private int luckyId;
    private int operation;

}
