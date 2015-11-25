package com.sogou.pay.web.form.notify;

import com.sogou.pay.common.constraint.PositiveNumber;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User: Liwei
 * Date: 15/3/16
 * Time: 下午4:56
 * Description: 财付通支付回调参数对象封装
 */
public class TenPayWebNotifyParams {

    /******基本参数*******/
    private String notify_id;

    @NotBlank
    private String sign_type;

    @NotBlank
    private String sign;

    /********业务参数**********/
    @NotBlank
    private String out_trade_no; //支付中心请求流水号

    @NotBlank
    @Length(max = 64)
    private String transaction_id; //支付宝交易号

    private String bank_billno; //银行流水号

    @NotBlank
    private String trade_state; //支付结果，0-成功，其它保留

    private String time_end; //交易付款时间

    @NotBlank
    @PositiveNumber
    private String total_fee;  //交易金额

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

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getBank_billno() {
        return bank_billno;
    }

    public void setBank_billno(String bank_billno) {
        this.bank_billno = bank_billno;
    }

    public String getTrade_state() {
        return trade_state;
    }

    public void setTrade_state(String trade_state) {
        this.trade_state = trade_state;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    @Override
    public String toString() {
        return "TenPayWebNotifyParams{" +
                "notify_id='" + notify_id + '\'' +
                ", sign_type='" + sign_type + '\'' +
                ", sign='" + sign + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", transaction_id='" + transaction_id + '\'' +
                ", bank_billno='" + bank_billno + '\'' +
                ", trade_state='" + trade_state + '\'' +
                ", time_end='" + time_end + '\'' +
                ", total_fee='" + total_fee + '\'' +
                '}';
    }
}
