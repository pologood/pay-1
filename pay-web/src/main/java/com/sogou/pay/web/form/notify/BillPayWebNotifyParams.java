package com.sogou.pay.web.form.notify;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User: Huangguoqing
 * Date: 15/7/9
 * Time: 下午4:56
 * Description: 快钱支付回调参数对象封装
 */
public class BillPayWebNotifyParams {

    @Length(max = 30)
    private String merchantAcctId; //商户账号

    @Length(max = 10)
    private String version; //版本号
    
    @Length(max = 2)
    private String language; //语言

    @NotBlank
    @Length(max = 2)
    private String signType; //签名类型 

    @Length(max = 2)
    private String payType; //支付方式

    @Length(max = 8)
    private String bankId; //银行代码

    @NotBlank
    @Length(max = 30)
    private String orderId; //商户订单号

    @Length(max = 14)
    private String orderTime; //商户订单提交时间

    @NotBlank
    @Length(max = 10)
    private String orderAmount; //商户订单金额

    @NotBlank
    @Length(max = 30)
    private String dealId;//快钱交易号
    
    @Length(max = 30)
    private String bankDealId;//银行交易号
    
    @Length(max = 14)
    private String dealTime;  //快 钱 交 易 时间
    
    @Length(max = 10)
    private String payAmount;//订 单 实 际 支付金额(分为单位)
    
    @Length(max = 10)
    private String fee;//费用(分为单位)
    
    @Length(max = 2)
    private String payResult;//处理结果
    
    @Length(max = 1024)
    private String signMsg;//签名字符串

    public String getMerchantAcctId() {
        return merchantAcctId;
    }

    public void setMerchantAcctId(String merchantAcctId) {
        this.merchantAcctId = merchantAcctId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getBankDealId() {
        return bankDealId;
    }

    public void setBankDealId(String bankDealId) {
        this.bankDealId = bankDealId;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPayResult() {
        return payResult;
    }

    public void setPayResult(String payResult) {
        this.payResult = payResult;
    }

    public String getSignMsg() {
        return signMsg;
    }

    public void setSignMsg(String signMsg) {
        this.signMsg = signMsg;
    }
    
}
