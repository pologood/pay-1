package com.sogou.pay.thirdpay.biz.model;

/**
 * 退票查询接口--返回cancel_rec实体
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/06/03 14:45
 */
public class RefundQueryRecord {
    private String draw_id;                   //提现单号
    private String package_id;                //批次号
    private String serial;                    //单笔序列号
    private String pay_amt;                   //付款金额
    private String bank_type;                 //银行编码
    private String draw_time;                 // 代付发起时间
    private String cancel_time;               //退票时间
    private String cancel_res;                //退票原因

    public String getDraw_id() {
        return draw_id;
    }

    public void setDraw_id(String draw_id) {
        this.draw_id = draw_id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getPay_amt() {
        return pay_amt;
    }

    public void setPay_amt(String pay_amt) {
        this.pay_amt = pay_amt;
    }

    public String getBank_type() {
        return bank_type;
    }

    public void setBank_type(String bank_type) {
        this.bank_type = bank_type;
    }

    public String getDraw_time() {
        return draw_time;
    }

    public void setDraw_time(String draw_time) {
        this.draw_time = draw_time;
    }

    public String getCancel_time() {
        return cancel_time;
    }

    public void setCancel_time(String cancel_time) {
        this.cancel_time = cancel_time;
    }

    public String getCancel_res() {
        return cancel_res;
    }

    public void setCancel_res(String cancel_res) {
        this.cancel_res = cancel_res;
    }
}