package com.sogou.pay.common.http;

import org.apache.http.Header;

/**
 * HTTP响应包装类
 */
public class Response {


  private Header[] headers;
  private int status;
  private String charset = "UTF-8";
  private byte[] data;

  public String getCharset() {
    return charset;
  }

  public void setCharset(String respCharset) {
    this.charset = respCharset;
  }

  public Header[] getHeaders() {
    return headers;
  }

  public void setHeaders(Header[] headers) {
    this.headers = headers;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public String getStringData() throws Exception {
    return new String(data, charset);
  }

}
