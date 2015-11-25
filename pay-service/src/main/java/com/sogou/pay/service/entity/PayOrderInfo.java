package com.sogou.pay.service.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author huangguoqing
 * @Date 2015/3/3 10:53
 * @Description: 支付单实体
 */
public class PayOrderInfo{
    //支付单ID
    private String payId;

    //订单类型 1：普通支付订单；2：余额充值订单；3：退款订单；4：其他订单
    private Integer orderType;

    //商户订单号
    private String orderId;

    //商品信息
    private String productInfo;

    //订单金额
    private BigDecimal orderMoney;

    //用户ID
    private String buyHomeAccount;

    //用户IP
    private String buyHomeIp;

    //卖家账号
    private Integer sellHomeAccount;

    //支付方式：1，PC在线支付 2，移动支付
    private Integer accessPlatForm;

    //支付渠道
    private String channelCode;

    //支付单状态：1，未支付；2，部份支付；3，支付完成；4，无效；
    private Integer payOrderStatus;

    //退款金额
    private BigDecimal refundMoney;

    //退款标识 1:未退款 2:部分退款 3:退款完成
    private Integer refundFlag;

    //订单生成时间
    private Date orderCreateTime;

    //创建时间
    private Date createTime;

    //支付完成时间
    private Date paySuccessTime;

    //业务平台ID
    private Integer appId;

    //通知商户状态 0:未通知 1:已通知
    private Integer notifyStatus;

    //业务平台页面通知
    private String appPageUrl;

    //业务平台点对点通知
    private String appBgUrl;

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public BigDecimal getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(BigDecimal orderMoney) {
        this.orderMoney = orderMoney;
    }

    public String getBuyHomeAccount() {
        return buyHomeAccount;
    }

    public void setBuyHomeAccount(String buyHomeAccount) {
        this.buyHomeAccount = buyHomeAccount;
    }

    public String getBuyHomeIp() {
        return buyHomeIp;
    }

    public void setBuyHomeIp(String buyHomeIp) {
        this.buyHomeIp = buyHomeIp;
    }

    public Integer getSellHomeAccount() {
        return sellHomeAccount;
    }

    public void setSellHomeAccount(Integer sellHomeAccount) {
        this.sellHomeAccount = sellHomeAccount;
    }

    public Integer getAccessPlatForm() {
        return accessPlatForm;
    }

    public void setAccessPlatForm(Integer accessPlatForm) {
        this.accessPlatForm = accessPlatForm;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Integer getPayOrderStatus() {
        return payOrderStatus;
    }

    public void setPayOrderStatus(Integer payOrderStatus) {
        this.payOrderStatus = payOrderStatus;
    }

    public BigDecimal getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(BigDecimal refundMoney) {
        this.refundMoney = refundMoney;
    }

    public Integer getRefundFlag() {
        return refundFlag;
    }

    public void setRefundFlag(Integer refundFlag) {
        this.refundFlag = refundFlag;
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

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(Integer notifyStatus) {
        this.notifyStatus = notifyStatus;
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
