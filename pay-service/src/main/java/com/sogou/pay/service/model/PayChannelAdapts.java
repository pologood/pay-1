package com.sogou.pay.service.model;

import java.util.List;

public class PayChannelAdapts {

    private List<com.sogou.pay.service.model.PayChannelAdapt> bankDebitList;//网银支付银行列表(储蓄卡)
    private List<com.sogou.pay.service.model.PayChannelAdapt> bankCreditList;//网银支付银行列表(信用卡)
    private List<com.sogou.pay.service.model.PayChannelAdapt> thirdPayList;//第三方支付列表
    private List<com.sogou.pay.service.model.PayChannelAdapt> qrCodeList;//扫码支付列表
    private List<com.sogou.pay.service.model.PayChannelAdapt> b2bList;//B2B支付列表

    public List<com.sogou.pay.service.model.PayChannelAdapt> getBankDebitList() {
        return bankDebitList;
    }

    public void setBankDebitList(List<com.sogou.pay.service.model.PayChannelAdapt> bankDebitList) {
        this.bankDebitList = bankDebitList;
    }

    public List<com.sogou.pay.service.model.PayChannelAdapt> getBankCreditList() {
        return bankCreditList;
    }

    public void setBankCreditList(List<com.sogou.pay.service.model.PayChannelAdapt> bankCreditList) {
        this.bankCreditList = bankCreditList;
    }

    public List<com.sogou.pay.service.model.PayChannelAdapt> getThirdPayList() {
        return thirdPayList;
    }

    public void setThirdPayList(List<com.sogou.pay.service.model.PayChannelAdapt> thirdPayList) {
        this.thirdPayList = thirdPayList;
    }

    public List<com.sogou.pay.service.model.PayChannelAdapt> getQrCodeList() {
        return qrCodeList;
    }

    public void setQrCodeList(List<com.sogou.pay.service.model.PayChannelAdapt> qrCodeList) {
        this.qrCodeList = qrCodeList;
    }

    public List<com.sogou.pay.service.model.PayChannelAdapt> getB2bList() {
        return b2bList;
    }

    public void setB2bList(List<com.sogou.pay.service.model.PayChannelAdapt> b2bList) {
        this.b2bList = b2bList;
    }
}
