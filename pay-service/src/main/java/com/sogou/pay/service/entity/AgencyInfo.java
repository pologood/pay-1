package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 支付机构对象
 *
 * @author 武景畔
 * @version 0.0.1
 */
public class AgencyInfo implements Serializable {

    private Integer id;
    /**
     * 支付机构编码
     */
    private String agencyCode;

    /**
     * 接入平台
     */
    private Integer accessPlatform;

    /**
     * 机构名称
     */
    private String agencyName;

    /**
     * 银行别名标示
     */
    private Integer aliasFlag;

    /**
     * 机构类型
     */
    private Integer agencyType;

    /**
     * 预支付URL
     */
    private String prepayUrl;

    /**
     * 支付URL
     */
    private String payUrl;

    /**
     * 查询URL
     */
    private String queryUrl;

    /**
     * 退款URL
     */
    private String refundUrl;

    /**
     * 退款查询URL
     */
    private String queryRefundUrl;

    /**
     * 下载对账单URL
     */
    private String downloadUrl;

    /**
     * 发手机验证码URL
     */
    private String sendPhoneUrl;

    //支付之后页面回调地址
    private String pageBackUrl;

    //支付之后服务后端回调地址
    private String notifyBackUrl;

    //退款之后服务后端回调地址
    private String refundNotifyBackUrl;

    //转账之后服务后端回调地址
    private String transferNotifyBackUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;


    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public Integer getAccessPlatform() {
        return accessPlatform;
    }

    public void setAccessPlatform(Integer accessPlatform) {
        this.accessPlatform = accessPlatform;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public Integer getAliasFlag() {
        return aliasFlag;
    }

    public void setAliasFlag(Integer aliasFlag) {
        this.aliasFlag = aliasFlag;
    }

    public Integer getAgencyType() {
        return agencyType;
    }

    public void setAgencyType(Integer agencyType) {
        this.agencyType = agencyType;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    public String getRefundUrl() {
        return refundUrl;
    }

    public void setRefundUrl(String refundUrl) {
        this.refundUrl = refundUrl;
    }

    public String getQueryRefundUrl() {
        return queryRefundUrl;
    }

    public void setQueryRefundUrl(String queryRefundUrl) {
        this.queryRefundUrl = queryRefundUrl;
    }

    public String getSendPhoneUrl() {
        return sendPhoneUrl;
    }

    public void setSendPhoneUrl(String sendPhoneUrl) {
        this.sendPhoneUrl = sendPhoneUrl;
    }

    public String getPrepayUrl() {
        return prepayUrl;
    }

    public void setPrepayUrl(String prepayUrl) {
        this.prepayUrl = prepayUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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

    public String getRefundNotifyBackUrl() {
        return refundNotifyBackUrl;
    }

    public void setRefundNotifyBackUrl(String refundNotifyBackUrl) {
        this.refundNotifyBackUrl = refundNotifyBackUrl;
    }

    public String getTransferNotifyBackUrl() {
        return transferNotifyBackUrl;
    }

    public void setTransferNotifyBackUrl(String transferNotifyBackUrl) {
        this.transferNotifyBackUrl = transferNotifyBackUrl;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AgencyInfo [agencyCode=" + agencyCode + ", accessPlatform="
                + accessPlatform + ", agencyName=" + agencyName
                + ", aliasFlag=" + aliasFlag + ", agencyType=" + agencyType
                + ", prepayUrl=" + prepayUrl + ", payUrl=" + payUrl
                + ", queryUrl=" + queryUrl + ", refundUrl=" + refundUrl
                + ", refundNotifyBackUrl=" + refundNotifyBackUrl + ", transferNotifyBackUrl=" + transferNotifyBackUrl
                + ", sendPhoneUrl=" + sendPhoneUrl + ", createTime="
                + createTime + ", modifyTime=" + modifyTime + "]";
    }

}
