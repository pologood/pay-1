package com.sogou.pay.manager.model.thirdpay;

/**
 * User: hujunfei
 * Date: 2015-03-03 10:07
 */
public class RefundThirdPayModel {
    private String agencyCode;
    private String merchantNo;
    private String refundUrl;
    private String refundNotifyUrl;
    private String securityKey;
    private String publicCertFilePath;
    private String privateCertFilePath;
    private String refundSerialNumber;
    private String refundReqTime;
    private String serialNumber;
    private String agencySerialNumber;
    private String refundAmount;
    private String totalAmount;

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getRefundUrl() {
        return refundUrl;
    }

    public void setRefundUrl(String refundUrl) {
        this.refundUrl = refundUrl;
    }

    public String getRefundNotifyUrl() {
        return refundNotifyUrl;
    }

    public void setRefundNotifyUrl(String refundNotifyUrl) {
        this.refundNotifyUrl = refundNotifyUrl;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getPublicCertFilePath() {
        return publicCertFilePath;
    }

    public void setPublicCertFilePath(String publicCertFilePath) {
        this.publicCertFilePath = publicCertFilePath;
    }

    public String getPrivateCertFilePath() {
        return privateCertFilePath;
    }

    public void setPrivateCertFilePath(String privateCertFilePath) {
        this.privateCertFilePath = privateCertFilePath;
    }

    public String getRefundSerialNumber() {
        return refundSerialNumber;
    }

    public void setRefundSerialNumber(String refundSerialNumber) {
        this.refundSerialNumber = refundSerialNumber;
    }

    public String getRefundReqTime() {
        return refundReqTime;
    }

    public void setRefundReqTime(String refundReqTime) {
        this.refundReqTime = refundReqTime;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAgencySerialNumber() {
        return agencySerialNumber;
    }

    public void setAgencySerialNumber(String agencySerialNumber) {
        this.agencySerialNumber = agencySerialNumber;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
