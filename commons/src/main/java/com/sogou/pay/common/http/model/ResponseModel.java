package com.sogou.pay.common.http.model;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-2
 * Time: 下午10:59
 */
public class ResponseModel {

    //返回码
    private int statusCode;

    //当{statusCode}为302或者301时，用于表示要跳转的地址
    private String location;

    //返回的内容
    private String body;


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
