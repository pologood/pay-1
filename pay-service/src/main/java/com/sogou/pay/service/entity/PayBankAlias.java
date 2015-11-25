package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author huangguoqing
 * @Date 2015/3/6 10:41
 * @Description: 银行别名信息
 */
public class PayBankAlias implements Serializable{
    //自增ID
    public Integer aliasId;

    //第三方支付机构编码
    public String agencyCode;

    //银行简称
    public String bankCode;

    //银行别名
    public String aliasName;

    //预留字段
    public String reserved;

    //创建时间
    public Date createTime;

    //修改时间
    public Date modifyTime;

    public Integer getAliasId() {
        return aliasId;
    }

    public void setAliasId(Integer aliasId) {
        this.aliasId = aliasId;
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

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
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
