package com.sogou.pay.service.entity;

import java.util.Date;

//业务线
public class App {

    private Integer id;              //自增ID
    private Integer appId;       //app的id
    private Integer companyId;   //所属公司 1：搜狗网络 2：搜过科技
    private Integer status;        //状态，1：启用 2：禁用
    private String appName;     //app名称
    private String signKey;   //签名key
    private String wxServiceNo;   //微信公众服务号
    private Date createTime;   //创建时间
    private Date modifyTime;     //修改时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public String getWxServiceNo() {
        return wxServiceNo;
    }

    public void setWxServiceNo(String wxServiceNo) {
        this.wxServiceNo = wxServiceNo;
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
