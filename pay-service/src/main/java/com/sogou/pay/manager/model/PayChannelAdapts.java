package com.sogou.pay.manager.model;

import java.util.List;

public class PayChannelAdapts {

    private List<PayChannelAdapt> bankDebitList;//网银支付银行列表(储蓄卡)
    private List<PayChannelAdapt> bankCreditList;//网银支付银行列表(信用卡)
    private List<PayChannelAdapt> thirdPayList;//第三方支付列表
    private List<PayChannelAdapt> qrCodeList;//扫码支付列表
    private List<PayChannelAdapt> b2bList;//B2B支付列表

    public List<PayChannelAdapt> getBankDebitList() {
        return bankDebitList;
    }

    public void setBankDebitList(List<PayChannelAdapt> bankDebitList) {
        this.bankDebitList = bankDebitList;
    }

    public List<PayChannelAdapt> getBankCreditList() {
        return bankCreditList;
    }

    public void setBankCreditList(List<PayChannelAdapt> bankCreditList) {
        this.bankCreditList = bankCreditList;
    }

    public List<PayChannelAdapt> getThirdPayList() {
        return thirdPayList;
    }

    public void setThirdPayList(List<PayChannelAdapt> thirdPayList) {
        this.thirdPayList = thirdPayList;
    }

    public List<PayChannelAdapt> getQrCodeList() {
        return qrCodeList;
    }

    public void setQrCodeList(List<PayChannelAdapt> qrCodeList) {
        this.qrCodeList = qrCodeList;
    }

    public List<PayChannelAdapt> getB2bList() {
        return b2bList;
    }

    public void setB2bList(List<PayChannelAdapt> b2bList) {
        this.b2bList = b2bList;
    }
}
