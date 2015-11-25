package com.sogou.pay.thirdpay.biz.modle;

/**
 * 批量银行代付实现接口--record实体
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/06/02 11:04
 */
public class Record {
    private String serial;                     //单笔序列号
    private String rec_bankacc;                //收款方银行帐号
    private String bank_type;                  //银行类型
    private String rec_name;                   //收款方真实姓名
    private String pay_amt;                    //付款金额
    private String acc_type;                   //账户类
    private String area;                       // 开户地区
    private String city;                       //开户城市
    private String subbank_name;               //支行名称
    private String desc;                       //付款说明
    private String recv_mobile;                //付款接收通知手机号


    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getRec_bankacc() {
        return rec_bankacc;
    }

    public void setRec_bankacc(String rec_bankacc) {
        this.rec_bankacc = rec_bankacc;
    }

    public String getBank_type() {
        return bank_type;
    }

    public void setBank_type(String bank_type) {
        this.bank_type = bank_type;
    }

    public String getRec_name() {
        return rec_name;
    }

    public void setRec_name(String rec_name) {
        this.rec_name = rec_name;
    }

    public String getPay_amt() {
        return pay_amt;
    }

    public void setPay_amt(String pay_amt) {
        this.pay_amt = pay_amt;
    }

    public String getAcc_type() {
        return acc_type;
    }

    public void setAcc_type(String acc_type) {
        this.acc_type = acc_type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSubbank_name() {
        return subbank_name;
    }

    public void setSubbank_name(String subbank_name) {
        this.subbank_name = subbank_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRecv_mobile() {
        return recv_mobile;
    }

    public void setRecv_mobile(String recv_mobile) {
        this.recv_mobile = recv_mobile;
    }

    @Override
    public String toString() {
        return "record{" +
                "serial=" + serial +
                ", rec_bankacc='" + rec_bankacc + '\'' +
                ", bank_type='" + bank_type + '\'' +
                ", rec_name='" + rec_name + '\'' +
                ", pay_amt='" + pay_amt + '\'' +
                ", acc_type='" + acc_type + '\'' +
                ", area='" + area + '\'' +
                ", city='" + city + '\'' +
                ", subbank_name='" + subbank_name + '\'' +
                ", desc='" + desc + '\'' +
                ", recv_mobile=" + recv_mobile +
                '}';
    }

    public String toXml() {
        return "<record>" +
                "<serial>" + serial + "</serial>" +
                "<rec_bankacc>" + rec_bankacc + "</rec_bankacc>" +
                "<bank_type>" + bank_type + "</bank_type>" +
                "<rec_name>" + rec_name + "</rec_name>" +
                "<pay_amt>" + pay_amt + "</pay_amt>" +
                "<acc_type>" + acc_type + "</acc_type>" +
                "<area>" + area + "</area>" +
                "<city>" + city + "</city>" +
                "<subbank_name>" + subbank_name + "</subbank_name>" +
                "<desc>" + desc + "</desc>" +
                "<recv_mobile>" + recv_mobile + "</recv_mobile>" + "</record>";
    }
}