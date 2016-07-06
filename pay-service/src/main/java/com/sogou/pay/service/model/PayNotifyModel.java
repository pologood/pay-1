package com.sogou.pay.service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PayNotifyModel {

    private String payDetailId; // 请求流水号、回调流水号，因为一对一的关系，所以都用这一个字段标识，好处是简单不用再生出id，坏处是复用字段定义容易混淆
    private String agencyOrderId; // 第三方支付机构流水号
    private String bankOrderId; //银行流水号
//    private int payStatus; // 支付状态：1，成功；2.失败
    @JsonFormat(pattern = "yyyyMMddHHmmss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyyMMddHHmmss")
    private Date agencyPayTime; //支付完成时间
    private BigDecimal trueMoney; //交易金额
    private String channelType;//支付渠道组合信息(支付宝专用)

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

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }
}
