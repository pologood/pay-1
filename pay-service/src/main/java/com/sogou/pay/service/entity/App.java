package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Liwei Date: 2014/12/25 Time: 10:15
 */
public class App implements Serializable {

    private Integer id;              //自增ID

    private String appName;     //app名称

    private Integer appId;       //app的id

    private Integer belongCompany;   //所属公司 1：搜狗网络 2：搜过科技

    private String signKey;   //签名key

    private Integer status;        //状态 0：失效 1：使用中

    private Date createTime;   //创建时间

    private Date modifyTime;     //修改时间

    private String wxServiceNo;   //微信公众服务号

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer Id) {
        this.appId = Id;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
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

    public Integer getBelongCompany() {
        return belongCompany;
    }

    public void setBelongCompany(Integer belongCompany) {
        this.belongCompany = belongCompany;
    }

    public String getWxServiceNo() {
        return wxServiceNo;
    }

    public void setWxServiceNo(String wxServiceNo) {
        this.wxServiceNo = wxServiceNo;
    }

    @Override
  public String toString() {
    return "App{" +
           "id=" + id +
           ", appName='" + appName + '\'' +
           ", appId='" + appId + '\'' +
           ", belongCompany='" + belongCompany + '\'' +
           ", signKey='" + signKey + '\'' +
           ", wxServiceNo='" + wxServiceNo + '\'' +
           ", createTime=" + createTime +
           '}';
  }
}
