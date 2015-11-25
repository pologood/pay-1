package com.sogou.pay.common.utils.httpclient;

import java.io.InputStream;

import org.apache.http.Header;

/**
 * @Author qibaichao
 * @ClassName Response
 * @Date 2014年9月12日
 * @Description:
 *               HTTP响应包装类
 */
public class Response {

    /**
     * 返回中的Header信息
     */
    private Header[] responseHeaders;

    /**
     * 返回内容的字符编码
     */
    private String respCharset = "UTF-8";

    /**
     * byte类型的result
     */
    private byte[] byteResult;

    public String getRespCharset() {
        return respCharset;
    }

    public void setRespCharset(String respCharset) {
        this.respCharset = respCharset;
    }

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public byte[] getByteResult() {
        if (byteResult != null) {
            return byteResult;
        }
        return null;
    }

    public void setByteResult(byte[] byteResult) {
        this.byteResult = byteResult;
    }

}
