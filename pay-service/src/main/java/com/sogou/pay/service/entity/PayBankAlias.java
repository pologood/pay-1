package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 银行别名
 */
public class PayBankAlias implements Serializable{
    //自增ID
    public Integer id;
    //第三方支付机构编码
    public String agencyCode;
    //银行简称
    public String bankCode;
    //银行别名
    public String aliasName;
    //创建时间
    public Date createTime;
    //修改时间
    public Date modifyTime;

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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
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
