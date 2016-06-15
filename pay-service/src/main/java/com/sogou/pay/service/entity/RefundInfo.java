package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RefundInfo implements Serializable {
    private String refundId;                // 退款单号
    private String payDetailId;             // 支付流水号
    private String payId;                   // 支付单号
    private String orderId;                 // 商户订单ID
    private int appId;                      // 业务线
    private String appBgUrl;                // 回调URL
    private String agencyCode;              // 支付机构编码
    private String merchantNo;              // 支付中心在第三方支付机构开设的商户号
    private String buyerAccount;            // 买家账户
    private int refundFeeType;                 // 退款方式
    private BigDecimal orderMoney;          // 订单金额
    private BigDecimal useBalance;          // 使用余额
    private BigDecimal refundMoney;         // 退款金额
    private BigDecimal bankRefund;          // 网银退款部分
    private BigDecimal balanceRefund;       // 余额退款部分
    private int refundStatus;               // 退款状态
    private int taskStatus;                 // 任务状态
    private String refundErrorCode;         // 退款错误码
    private String refundErrorInfo;         // 退款错误信息
    private Date refundReqTime;             // 退款请求时间
    private Date refundResTime;             // 退款完成时间
    private int checkDate;                  // 对账日期

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getPayDetailId() {
        return payDetailId;
    }

    public void setPayDetailId(String payDetailId) {
        this.payDetailId = payDetailId;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getAppBgUrl() {
        return appBgUrl;
    }

    public void setAppBgUrl(String appBgUrl) {
        this.appBgUrl = appBgUrl;
    }

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

    public String getBuyerAccount() {
        return buyerAccount;
    }

    public void setBuyerAccount(String buyerAccount) {
        this.buyerAccount = buyerAccount;
    }

    public int getRefundFeeType() {
        return refundFeeType;
    }

    public void setRefundFeeType(int refundFeeType) {
        this.refundFeeType = refundFeeType;
    }

    public BigDecimal getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(BigDecimal orderMoney) {
        this.orderMoney = orderMoney;
    }

    public BigDecimal getUseBalance() {
        return useBalance;
    }

    public void setUseBalance(BigDecimal useBalance) {
        this.useBalance = useBalance;
    }

    public BigDecimal getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(BigDecimal refundMoney) {
        this.refundMoney = refundMoney;
    }

    public BigDecimal getBankRefund() {
        return bankRefund;
    }

    public void setBankRefund(BigDecimal bankRefund) {
        this.bankRefund = bankRefund;
    }

    public BigDecimal getBalanceRefund() {
        return balanceRefund;
    }

    public void setBalanceRefund(BigDecimal balanceRefund) {
        this.balanceRefund = balanceRefund;
    }

    public int getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(int refundStatus) {
        this.refundStatus = refundStatus;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getRefundErrorCode() {
        return refundErrorCode;
    }

    public void setRefundErrorCode(String refundErrorCode) {
        this.refundErrorCode = refundErrorCode;
    }

    public String getRefundErrorInfo() {
        return refundErrorInfo;
    }

    public void setRefundErrorInfo(String refundErrorInfo) {
        this.refundErrorInfo = refundErrorInfo;
    }

    public Date getRefundReqTime() {
        return refundReqTime;
    }

    public void setRefundReqTime(Date refundReqTime) {
        this.refundReqTime = refundReqTime;
    }

    public Date getRefundResTime() {
        return refundResTime;
    }

    public void setRefundResTime(Date refundResTime) {
        this.refundResTime = refundResTime;
    }

    public int getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(int checkDate) {
        this.checkDate = checkDate;
    }
}
