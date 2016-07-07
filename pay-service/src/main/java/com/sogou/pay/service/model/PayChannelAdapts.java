package com.sogou.pay.service.model;

import com.sogou.pay.service.entity.PayChannel;

import java.util.List;

public class PayChannelAdapts {

    private List<PayChannel> bankDebitList;//网银支付银行列表(储蓄卡)
    private List<PayChannel> bankCreditList;//网银支付银行列表(信用卡)
    private List<PayChannel> thirdPayList;//第三方支付列表
    private List<PayChannel> qrCodeList;//扫码支付列表
    private List<PayChannel> b2bList;//B2B支付列表

    public List<PayChannel> getBankDebitList() {
        return bankDebitList;
    }

    public void setBankDebitList(List<PayChannel> bankDebitList) {
        this.bankDebitList = bankDebitList;
    }

    public List<PayChannel> getBankCreditList() {
        return bankCreditList;
    }

    public void setBankCreditList(List<PayChannel> bankCreditList) {
        this.bankCreditList = bankCreditList;
    }

    public List<PayChannel> getThirdPayList() {
        return thirdPayList;
    }

    public void setThirdPayList(List<PayChannel> thirdPayList) {
        this.thirdPayList = thirdPayList;
    }

    public List<PayChannel> getQrCodeList() {
        return qrCodeList;
    }

    public void setQrCodeList(List<PayChannel> qrCodeList) {
        this.qrCodeList = qrCodeList;
    }

    public List<PayChannel> getB2bList() {
        return b2bList;
    }

    public void setB2bList(List<PayChannel> b2bList) {
        this.b2bList = b2bList;
    }
}
