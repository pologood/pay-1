package com.sogou.pay.web.form.notify;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.sogou.pay.common.constraint.PositiveNumber;

/**
 * User: Liwei
 * Date: 15/3/3
 * Time: 下午4:56
 * Description: 支付宝支付回调参数对象封装
 */
public class AliPayWebNotifyParams {

    /******基本参数*******/
    private String notify_id;

    @NotBlank
    private String sign_type;

    @NotBlank
    private String sign;

    /********业务参数**********/
    @NotBlank
    @Length(max = 30)

    private String out_trade_no; //支付中心请求流水号

    @NotBlank
    @Length(max = 64)
    private String trade_no; //支付宝交易号

    private String bank_order_id; //银行流水号，支付宝不传

    @NotBlank
    private String trade_status; //交易状态, 默认TRADE_FINISHED

    private String gmt_payment; //交易付款时间

    @NotBlank
    @PositiveNumber
    private String total_fee;  //交易金额

    @NotBlank
    private String out_channel_type;//交易渠道组合信息
    
    public String getOut_channel_type() {
        return out_channel_type;
    }

    public void setOut_channel_type(String out_channel_type) {
        this.out_channel_type = out_channel_type;
    }

    public String getGmt_payment() {
        return gmt_payment;
    }

    public void setGmt_payment(String gmt_payment) {
        this.gmt_payment = gmt_payment;
    }

    public String getNotify_id() {
        return notify_id;
    }

    public void setNotify_id(String notify_id) {
        this.notify_id = notify_id;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getTrade_status() {
        return trade_status;
    }

    public void setTrade_status(String trade_status) {
        this.trade_status = trade_status;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }
    public String getBank_order_id() {
        return bank_order_id;
    }

    public void setBank_order_id(String bank_order_id) {
        this.bank_order_id = bank_order_id;
    }
}
