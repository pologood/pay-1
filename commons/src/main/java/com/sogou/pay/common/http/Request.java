package com.sogou.pay.common.http;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求信息包装类
 */
public class Request {
  public static int GET = 0;
  public static int POST = 1;
  public SSLContext sslContext = null;
  private String charset = "UTF-8";
  /**
   * url,将忽略scheme、host、path的设置
   */
  private String url = null;
  private String scheme = "http";
  private String host = null;
  private String path = "";
  private boolean excludeEmptyValue = false;
  private List<NameValuePair> parameters = new ArrayList<NameValuePair>();
  private String requestBody = null;
  private int method = Request.GET;
  private int connectionTimeout = 0;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getScheme() {
    return scheme;
  }

  public void setScheme(String scheme) {
    this.scheme = scheme;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getMethod() {
    return method;
  }

  public void setMethod(int method) {
    this.method = method;
  }

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public void addParam(String key, String value) {
    if (key != null) {
      parameters.add(new BasicNameValuePair(key, value));
    }
  }

  public void addParam(Map<String, ?> paramMap) {

    for (String key : paramMap.keySet()) {
      if (paramMap.get(key) != null)
        parameters.add(new BasicNameValuePair(key, paramMap.get(key).toString()));
    }
  }

  public List<NameValuePair> getNameValuePairList() {
    return parameters;
  }

  public String getRequestBody() {
    return requestBody;
  }

  public void setRequestBody(String requestBody) {
    this.requestBody = requestBody;
  }

  public boolean isExcludeEmptyValue() {
    return excludeEmptyValue;
  }

  public void setExcludeEmptyValue(boolean excludeEmptyValue) {
    this.excludeEmptyValue = excludeEmptyValue;
  }

  public SSLContext getSslContext() {
    return sslContext;
  }

  public void setSslContext(SSLContext sslContext) {
    this.sslContext = sslContext;
  }

}
