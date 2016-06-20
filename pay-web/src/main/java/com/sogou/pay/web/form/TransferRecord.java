package com.sogou.pay.web.form;

import javax.validation.constraints.Digits;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.sogou.pay.common.constraint.Amount;



/**
 * @Author	huangguoqing 
 * @ClassName	PayParams 
 * @Date	2015年2月28日 
 * @Description:支付参数
 */
public class TransferRecord {

    @NotBlank(message = "代付单号不能为空！")
    @Length(max = 32,message = "代付单号最长为32位！")
    private String payId;
    
    @NotBlank(message = "收款账号不能为空！")
    @Digits(integer=32,fraction=0,message = "收款账号必须为整数，最长32位！")
    private String recBankacc;
    
    @NotBlank(message = "收款人不能为空！")
    @Length(max = 60,message = "收款人最长为60位！")
    private String recName;
    
    @Amount(message="付款金额不符合金额格式！")
    private String payAmt;
    
    @Length(max = 1,message = "系统内表示最长为一位，Y或N！")
    private String bankFlg;//系统内表示  Y:开户行是招行  N:开户行是他行
    
    @Length(max = 16,message = "他行开户行最长为16位！")
    private String eacBank;//他行开户行
    
    @Length(max = 32,message = "他行开户地最长为32位！")
    private String eacCity;//他行开开户地
    
    @Length(max = 100,message = "付款说明最长为100位！")
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
