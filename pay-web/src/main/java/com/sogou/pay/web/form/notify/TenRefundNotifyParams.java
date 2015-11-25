package com.sogou.pay.web.form.notify;

/**
 * User: hujunfei
 * Date: 2015-03-06 18:13
 */
public class TenRefundNotifyParams extends BaseRefundNotifyParams {
    private String sign_type;
    private String service_version;
    private String input_charset;
    private String Sign;
    private String sign_key_index;
    private String Partner;
    private String transaction_id;
    private String out_trade_no;
    private String out_refund_no;
    private String refund_id;
    private String refund_channel;
    private String refund_fee;
    private String refund_status;
    private String recv_user_id;
    private String reccv_user_name;

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getService_version() {
        return service_version;
    }

    public void setService_version(String service_version) {
        this.service_version = service_version;
    }

    public String getInput_charset() {
        return input_charset;
    }

    public void setInput_charset(String input_charset) {
        this.input_charset = input_charset;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public String getSign_key_index() {
        return sign_key_index;
    }

    public void setSign_key_index(String sign_key_index) {
        this.sign_key_index = sign_key_index;
    }

    public String getPartner() {
        return Partner;
    }

    public void setPartner(String partner) {
        Partner = partner;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public String getRefund_channel() {
        return refund_channel;
    }

    public void setRefund_channel(String refund_channel) {
        this.refund_channel = refund_channel;
    }

    public String getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(String refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getRefund_status() {
        return refund_status;
    }

    public void setRefund_status(String refund_status) {
        this.refund_status = refund_status;
    }

    public String getRecv_user_id() {
        return recv_user_id;
    }

    public void setRecv_user_id(String recv_user_id) {
        this.recv_user_id = recv_user_id;
    }

    public String getReccv_user_name() {
        return reccv_user_name;
    }

    public void setReccv_user_name(String reccv_user_name) {
        this.reccv_user_name = reccv_user_name;
    }
}
