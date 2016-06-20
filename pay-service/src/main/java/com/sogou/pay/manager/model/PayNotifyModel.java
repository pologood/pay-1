package com.sogou.pay.manager.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Liwei
 * Date: 15/3/9
 * Time: 下午12:46
 * Description: Notify Controller层传递对象封装
 */
public class PayNotifyModel {

    /**
     * **回调传入参数*****
     */
    private String payDetailId; // 请求流水号、回调流水号，因为一对一的关系，所以都用这一个字段标识，好处是简单不用再生出id，坏处是复用字段定义容易混淆
    private String agencyOrderId; // 第三方支付机构流水号
    private String bankOrderId; //银行流水号
//    private int payStatus; // 支付状态：1，成功；2.失败
    private Date agencyPayTime; //支付完成时间
    private BigDecimal trueMoney; //交易金额
    private String channelType;//支付渠道组合信息(支付宝专用)
    /**
     * *非数据库查询参数设定，如默认参数、平台参数等***
     */
    private String refundCode;  //这个版本中这个参数暂时不考虑


    public String getPayDetailId() {
        return payDetailId;
    }

    public void setPayDetailId(String payDetailId) {
        this.payDetailId = payDetailId;
    }

    public String getAgencyOrderId() {
        return agencyOrderId;
    }

    public void setAgencyOrderId(String agencyOrderId) {
        this.agencyOrderId = agencyOrderId;
    }

    public String getBankOrderId() {
        return bankOrderId;
    }

    public void setBankOrderId(String bankOrderId) {
        this.bankOrderId = bankOrderId;
    }

//    public int getPayStatus() {
//        return payStatus;
//    }
//
//    public void setPayStatus(int payStatus) {
//        this.payStatus = payStatus;
//    }

    public Date getAgencyPayTime() {
        return agencyPayTime;
    }

    public void setAgencyPayTime(Date agencyPayTime) {
        this.agencyPayTime = agencyPayTime;
    }

    public BigDecimal getTrueMoney() {
        return trueMoney;
    }

    public void setTrueMoney(BigDecimal trueMoney) {
        this.trueMoney = trueMoney;
    }

    public String getRefundCode() {
        return refundCode;
    }

    public void setRefundCode(String refundCode) {
        this.refundCode = refundCode;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }
}
