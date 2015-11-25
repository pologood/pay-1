package com.sogou.pay.web.form.notify;

import org.hibernate.validator.constraints.NotBlank;

/**
 * User: Hgq
 * Date: 15/5/22
 * Time: 下午4:56
 * Description: 支付宝Wap支付回调参数对象封装
 */
public class AliPayWapNotifyParams {

    /******基本参数*******/
    @NotBlank 
    private String service;

    @NotBlank
    private String v;
    
    @NotBlank
    private String sec_id;
    
    @NotBlank
    private String sign;

    /********业务参数**********/
    @NotBlank
    private String notify_data;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getSec_id() {
        return sec_id;
    }

    public void setSec_id(String sec_id) {
        this.sec_id = sec_id;
    }

    public String getNotify_data() {
        return notify_data;
    }

    public void setNotify_data(String notify_data) {
        this.notify_data = notify_data;
    }
    
}
