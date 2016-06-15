package com.sogou.pay.manager.model;

/**
 * @Author qibaichao
 * @ClassName PayCheckUpdateVo
 * @Date 2015年2月16日
 * @Description:
 */
public class PayCheckUpdateModel {

    private String instructId;

    private int payCheckStatus;

    private int payCheckWaitingStatus;

    private long payCheckId;

    private int appId;

    public String getInstructId() {
        return instructId;
    }

    public void setInstructId(String instructId) {
        this.instructId = instructId;
    }

    public int getPayCheckStatus() {
        return payCheckStatus;
    }

    public void setPayCheckStatus(int payCheckStatus) {
        this.payCheckStatus = payCheckStatus;
    }

    public int getPayCheckWaitingStatus() {
        return payCheckWaitingStatus;
    }

    public void setPayCheckWaitingStatus(int payCheckWaitingStatus) {
        this.payCheckWaitingStatus = payCheckWaitingStatus;
    }

    public long getPayCheckId() {
        return payCheckId;
    }

    public void setPayCheckId(long payCheckId) {
        this.payCheckId = payCheckId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }
}
