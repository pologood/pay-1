package com.sogou.pay.common.http.client;

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
    private String charset = "UTF-8";

    /**
     * byte类型的result
     */
    private byte[] byteData;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String respCharset) { this.charset = respCharset; }

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public byte[] getByteData() { return byteData; }

    public void setByteData(byte[] byteData) { this.byteData = byteData; }

}
