package com.sogou.pay.common.utils.httpclient;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @Author qibaichao
 * @ClassName MerchantResponse
 * @Date 2014年9月12日
 * @Description:商户响应信息封装
 */
public class MerchantResponse {

    private boolean success = false;

    private String respCharset = "UTF-8";

    private int code;

    private String message;

    private byte[] data;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStringResult() {
        if (data != null) {
            try {
                return new String(data, respCharset);
                
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }



}
