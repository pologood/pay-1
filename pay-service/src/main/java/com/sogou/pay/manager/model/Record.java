package com.sogou.pay.manager.model;

/**
 * @Author	huangguoqing 
 * @ClassName	PayParams 
 * @Date	2015年2月28日 
 * @Description:支付参数
 */
public class Record {

    private String payId;
    
    private String recBankacc;
    
    private String recName;
    
    private String payAmt;
    
    private String bankFlg;//系统内表示  Y:开户行是招行  N:开户行是他行
    
    private String eacBank;//他行开户行
    
    private String eacCity;//他行开开户地
    
    private String desc;//付款说明
    
    public String getRecBankacc() {
        return recBankacc;
    }

    public void setRecBankacc(String recBankacc) {
        this.recBankacc = recBankacc;
    }

    public String getRecName() {
        return recName;
    }

    public void setRecName(String recName) {
        this.recName = recName;
    }

    public String getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(String payAmt) {
        this.payAmt = payAmt;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getBankFlg() {
        return bankFlg;
    }

    public void setBankFlg(String bankFlg) {
        this.bankFlg = bankFlg;
    }

    public String getEacBank() {
        return eacBank;
    }

    public void setEacBank(String eacBank) {
        this.eacBank = eacBank;
    }

    public String getEacCity() {
        return eacCity;
    }

    public void setEacCity(String eacCity) {
        this.eacCity = eacCity;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    
}
