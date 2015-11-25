package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author huangguoqing
 * @Date 2015/3/4 13:29
 * @Description: 支付机构商户信息
 */
public class PayAgencyMerchant implements Serializable{

    //ID
    private Integer id;

    //业务平台ID
    private Integer appId;
    
    //支付机构编码
    private String agencyCode;

    //所属公司编码 (1:搜狗网络 2:搜狗科技)
    private Integer companyCode;

    //商户号
    private String merchantNo;

    //第三方开设的收款账号对应（邮箱）或者为微信公众号
    private String sellerEmail;

    //加密方式 (1：签名；2：非对称加密 3：对账加密)
    private Integer encryptionType;

    //加密密钥
    private String encryptKey;

    //第三方公钥证书路径
    private String pubKeypath;

    //本地私钥证书路径
    private String privateKeypath;

    //支付之后页面回调地址
    private String pageBackUrl;

    //支付之后服务后端回调地址
    private String notifyBackUrl;

    //是否启用 (0:未启用  1:已启用)
    private Integer isUsed;

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

    public Integer getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(Integer companyCode) {
        this.companyCode = companyCode;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public Integer getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(Integer encryptionType) {
        this.encryptionType = encryptionType;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getPubKeypath() {
        return pubKeypath;
    }

    public void setPubKeypath(String pubKeypath) {
        this.pubKeypath = pubKeypath;
    }

    public String getPrivateKeypath() {
        return privateKeypath;
    }

    public void setPrivateKeypath(String privateKeypath) {
        this.privateKeypath = privateKeypath;
    }

    public String getPageBackUrl() {
        return pageBackUrl;
    }

    public void setPageBackUrl(String pageBackUrl) {
        this.pageBackUrl = pageBackUrl;
    }

    public String getNotifyBackUrl() {
        return notifyBackUrl;
    }

    public void setNotifyBackUrl(String notifyBackUrl) {
        this.notifyBackUrl = notifyBackUrl;
    }

    public Integer getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Integer isUsed) {
        this.isUsed = isUsed;
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

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }
}
