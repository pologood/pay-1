package com.sogou.pay.web.form.notify;

/**
 * User: hujunfei
 * Date: 2015-03-04 16:38
 * 支付宝异步回调参数
 */
public class AliRefundNotifyParams {
    private String notify_time;
    private String notify_type;
    private String notify_id;
    private String sign_type;
    private String sign;
    private String batch_no;
    private String success_num;
    private String result_details;
    private String unfreezed_details;

    public String getNotify_time() {
        return notify_time;
    }

    public void setNotify_time(String notify_time) {
        this.notify_time = notify_time;
    }

    public String getNotify_type() {
        return notify_type;
    }

    public void setNotify_type(String notify_type) {
        this.notify_type = notify_type;
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

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getSuccess_num() {
        return success_num;
    }

    public void setSuccess_num(String success_num) {
        this.success_num = success_num;
    }

    public String getResult_details() {
        return result_details;
    }

    public void setResult_details(String result_details) {
        this.result_details = result_details;
    }

    public String getUnfreezed_details() {
        return unfreezed_details;
    }

    public void setUnfreezed_details(String unfreezed_details) {
        this.unfreezed_details = unfreezed_details;
    }
}
