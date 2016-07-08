package com.sogou.pay.service.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付单
 */
public class PayOrderInfo{
    //业务平台ID
    private Integer appId;
    //支付方式
    private Integer accessPlatform;
    //支付单ID
    private String payId;
    //商户订单号
    private String orderId;
    //支付渠道
    private String channelCode;
    //商品信息
    private String productInfo;
    //买家ID
    private String buyerAccount;
    //买家IP
    private String buyerIp;
    //卖家账号
    private Integer sellerAccount;
    //订单金额
    private BigDecimal orderMoney;
    //退款金额
    private BigDecimal refundMoney;
    //订单生成时间
    private Date orderCreateTime;
    //创建时间
    private Date createTime;
    //支付完成时间
    private Date paySuccessTime;
    //支付单状态
    private Integer payStatus;
    //退款标识
    private Integer refundFlag;
    //通知商户状态
    private Integer notifyStatus;
    //订单类型
    private Integer orderType;
    //业务线页面跳转同步通知地址
    private String appPageUrl;
    //业务线异步通知地址
    private String appBgUrl;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getAccessPlatform() {
        return accessPlatform;
    }

    public void setAccessPlatform(Integer accessPlatform) {
        this.accessPlatform = accessPlatform;
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

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public String getBuyerAccount() {
        return buyerAccount;
    }

    public void setBuyerAccount(String buyerAccount) {
        this.buyerAccount = buyerAccount;
    }

    public String getBuyerIp() {
        return buyerIp;
    }

    public void setBuyerIp(String buyerIp) {
        this.buyerIp = buyerIp;
    }

    public Integer getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(Integer sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

    public BigDecimal getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(BigDecimal orderMoney) {
        this.orderMoney = orderMoney;
    }

    public BigDecimal getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(BigDecimal refundMoney) {
        this.refundMoney = refundMoney;
    }

    public Date getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(Date orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPaySuccessTime() {
        return paySuccessTime;
    }

    public void setPaySuccessTime(Date paySuccessTime) {
        this.paySuccessTime = paySuccessTime;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getRefundFlag() {
        return refundFlag;
    }

    public void setRefundFlag(Integer refundFlag) {
        this.refundFlag = refundFlag;
    }

    public Integer getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(Integer notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getAppPageUrl() {
        return appPageUrl;
    }

    public void setAppPageUrl(String appPageUrl) {
        this.appPageUrl = appPageUrl;
    }

    public String getAppBgUrl() {
        return appBgUrl;
    }

    public void setAppBgUrl(String appBgUrl) {
        this.appBgUrl = appBgUrl;
    }
}
