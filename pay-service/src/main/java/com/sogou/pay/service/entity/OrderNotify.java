package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Liwei
 * Date: 2015/3/11
 * Time: 15:13
 */
public class OrderNotify implements Serializable {
    private int id;        //主键id
    private String taskId;//任务id
    private String job;          //回调应用实体
    private int appId;     //应用id
    private String owner;      //定时任务workerid
    private int notifyNum;    //回调次数
    private Date expectTime;  //下次执行时间
    private Date updateTime;  //修改时间


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getNotifyNum() {
        return notifyNum;
    }

    public void setNotifyNum(int notifyNum) {
        this.notifyNum = notifyNum;
    }

    public Date getExpectTime() {
        return expectTime;
    }

    public void setExpectTime(Date expectTime) {
        this.expectTime = expectTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "OrderNotify{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", job='" + job + '\'' +
                ", appId=" + appId +
                ", owner='" + owner + '\'' +
                ", notifyNum=" + notifyNum +
                ", expect_time=" + expectTime +
                ", updateTime=" + updateTime +
                '}';
    }

}
