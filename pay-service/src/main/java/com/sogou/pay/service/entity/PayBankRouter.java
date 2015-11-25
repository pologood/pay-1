package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author huangguoqing
 * @Date 2015/3/3 17:26
 * @Description: 银行路由实体
 */
public class PayBankRouter implements Serializable{
    //自增ID
    private Integer id;

    //银行编码
    private String bankCode;

    //银行卡类型(1:储蓄卡 2:信用卡 3:不区分)
    private Integer bankCardType;

    //业务ID
    private Integer appId;

    //第三方支付机构编码
    private String agencyCode;

    //支付概率百分比 0.5代表50%
    private double scale;

    //状态 1:启用 2:未启用
    private Integer routerStatus;

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

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public Integer getRouterStatus() {
        return routerStatus;
    }

    public void setRouterStatus(Integer routerStatus) {
        this.routerStatus = routerStatus;
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public Integer getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(Integer bankCardType) {
        this.bankCardType = bankCardType;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }
}
