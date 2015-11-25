package com.sogou.pay.manager.model;

import com.sogou.pay.common.annotation.MapField;

/**
 * Created by hujunfei Date: 15-1-4 Time: 下午12:08
 * 对应用传递参数的封装
 */
public class WebPayModel {
    @MapField(key = "service")
    private String agency;         // 第三方支付平台
    @MapField(key = "paymethod")
    private String channel;      // 接口类型，如快捷/网银/第三方账户
    @MapField(key = "order_id")
    private String apporderid;           // 应用订单ID

    private int appid;             // 应用ID
    private String terminal;   // 终端类型
    private String paygate;      // 支付网关类型，如银行代码
    private String username;          // 用户名
    private String product;           // 商品名称
    private long amount;            // 总金额
    private String return_url;        // 回调地址

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPaygate() {
        return paygate;
    }

    public void setPaygate(String paygate) {
        this.paygate = paygate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApporderid() {
        return apporderid;
    }

    public void setApporderid(String apporderid) {
        this.apporderid = apporderid;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    private String appdata;     // 透传数据

    public String getAppdata() {
        return appdata;
    }

    public void setAppdata(String appdata) {
        this.appdata = appdata;
    }
}
