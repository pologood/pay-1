package com.sogou.pay.thirdpay.biz.model;

/**
 * 批量银行代付查询接口--返回record实体
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/06/03 10:07
 */
public class QueryReturnRecord {
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
    private String modify_time;                //最后修改时间，格式：yyyy-MM-dd HH:mm:ss
    private String err_code;                   //付款失败错误码
    private String err_msg;                    //付款失败中文描述

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

    public String getModify_time() {
        return modify_time;
    }

    public void setModify_time(String modify_time) {
        this.modify_time = modify_time;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

}