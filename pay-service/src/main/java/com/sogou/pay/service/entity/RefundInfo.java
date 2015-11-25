package com.sogou.pay.service.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hjf on 15-3-2.
 */
public class RefundInfo implements Serializable {
    private int appId;
    private String appBgUrl;               // 回调URL
    private String refundId;                // 退款单号
    private String payDetailId;             // 支付流水号
    private String payId;                   // 支付单号
    private BigDecimal orderMoney;          // 订单金额
    private String orderId;                 // 商户订单ID
    private String buyHomeAccount;          // 用户账户
    private String agencyCode;              // 支付机构编码
    private String merchantNo;              // 支付中心在第三方支付机构开设的商户号
    private BigDecimal useBalance;          // 使用余额？
    private int payFeeType;                 // 退款方式
    private BigDecimal refundMoney;         // 退款金额
    private BigDecimal netBalanceRefund;    // 网银退款部分
    private BigDecimal balanceRefund;       // 余额退款部分
    private int refundStatus;               // 退款状态
    private int taskStatus;                 // 任务状态
    private String refundErrorCode;         // 退款错误码
    private String refundErrorInfo;         // 退款错误信息
    private Date refundReqTime;             // 退款请求时间
    private Date refundResTime;             // 退款完成时间
    private int checkDate;                  // 对账日期

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

    public BigDecimal getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(BigDecimal orderMoney) {
        this.orderMoney = orderMoney;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBuyHomeAccount() {
        return buyHomeAccount;
    }

    public void setBuyHomeAccount(String buyHomeAccount) {
        this.buyHomeAccount = buyHomeAccount;
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

    public BigDecimal getUseBalance() {
        return useBalance;
    }

    public void setUseBalance(BigDecimal useBalance) {
        this.useBalance = useBalance;
    }

    public int getPayFeeType() {
        return payFeeType;
    }

    public void setPayFeeType(int payFeeType) {
        this.payFeeType = payFeeType;
    }

    public BigDecimal getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(BigDecimal refundMoney) {
        this.refundMoney = refundMoney;
    }

    public BigDecimal getNetBalanceRefund() {
        return netBalanceRefund;
    }

    public void setNetBalanceRefund(BigDecimal netBalanceRefund) {
        this.netBalanceRefund = netBalanceRefund;
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
