package com.sogou.pay.common.http.client;

/**
 * Created by xiepeidong on 2016/1/15.
 */

import java.net.URI;
import java.nio.charset.Charset;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

/**
 * HTTP调用工具类
 */
public class HttpCaller {

  private static final Logger logger = LoggerFactory.getLogger(HttpCaller.class);
  private static final int DEFAULT_CONNECTIONT_IMEOUT = 8000;
  private static HttpCaller httpCaller = new HttpCaller();

  private HttpCaller() {
  }

  public static HttpCaller getInstance() {
    return httpCaller;
  }

  public Response call(Request request) {
    CloseableHttpClient httpClient = getHttpClient(request.getConnectionTimeout(), request.getSslContext());
    Response response = new Response();
    HttpRequestBase httpRequestBase = null;
    CloseableHttpResponse httpResponse = null;
    try {
      if (request.getMethod() == Request.GET) {
        httpRequestBase = buildHttpGet(request);
      } else if (request.getMethod() == Request.POST) {
        httpRequestBase = buildHttpPost(request);
      }
      httpResponse = httpClient.execute(httpRequestBase);
      HttpEntity entity = httpResponse.getEntity();
      Charset charset = ContentType.getOrDefault(entity).getCharset();
      if (charset == null)
        response.setCharset(request.getCharset());
      else
        response.setCharset(charset.name());
      response.setHeaders(httpResponse.getAllHeaders());
      response.setStatus(httpResponse.getStatusLine().getStatusCode());
      response.setData(EntityUtils.toByteArray(entity));
      if (response.getStatus() != 200)
        throw new RuntimeException("[HttpCaller.call] status=" +
                response.getStatus() + ", data=" + response.getStringData());
    } catch (Exception e) {
      if (httpRequestBase != null) {
        httpRequestBase.abort();
      }
      throw new RuntimeException(e);
    } finally {
      // 释放连接
      HttpClientUtils.closeQuietly(httpResponse);
    }
    return response;
  }

  private CloseableHttpClient getHttpClient(int connectionTimeout, SSLContext sslContext) {

    RequestConfig defaultRequestConfig = RequestConfig.custom().setExpectContinueEnabled(true)
            .setConnectTimeout(connectionTimeout == 0 ? DEFAULT_CONNECTIONT_IMEOUT : connectionTimeout)
            .setConnectionRequestTimeout(connectionTimeout == 0 ? DEFAULT_CONNECTIONT_IMEOUT : connectionTimeout)
            .build();
    HostnameVerifier hostnameVerifier = new HostnameVerifier() {
      public boolean verify(String var1, SSLSession var2) {
        return true;
      }
    };
    CloseableHttpClient httpClient = null;
    if (sslContext == null) {
      httpClient = HttpClients.custom()//.setConnectionManager(connectionManager)
              .setDefaultRequestConfig(defaultRequestConfig).setSSLHostnameVerifier(hostnameVerifier).build();
    } else {
      httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).setSSLContext(sslContext)
              .setSSLHostnameVerifier(hostnameVerifier).build();
    }
    return httpClient;
  }

  private HttpGet buildHttpGet(Request request) throws Exception {
    URIBuilder builder = null;
    if (request.getUrl() != null) {
      builder = new URIBuilder(request.getUrl());
    } else {
      builder = new URIBuilder();
      builder.setScheme(request.getScheme()).setHost(request.getHost()).setPath(request.getPath());
    }
    if (request.getRequestBody() != null) {
      builder.setCustomQuery(request.getRequestBody());
    } else {
      for (NameValuePair pair : request.getNameValuePairList()) {
        String value = pair.getValue();
        if (request.isExcludeEmptyValue() && (value == null || "".equals(value))) {
          continue;
        }
        builder.addParameter(pair.getName(), value);
      }
    }
    URI uri = builder.build();
    logger.info("[buildHttpGet] 构造uri={}", uri.toString());

    HttpGet httpGet = new HttpGet(uri);
    return httpGet;
  }

  private HttpPost buildHttpPost(Request request) throws Exception {
    HttpPost httpPost;
    URIBuilder builder = null;
    if (request.getUrl() != null) {
      builder = new URIBuilder(request.getUrl());
    } else {
      builder = new URIBuilder();
      builder.setScheme(request.getScheme()).setHost(request.getHost()).setPath(request.getPath());
    }
    HttpEntity entity = null;
    if (request.getRequestBody() != null) {
      entity = new StringEntity(request.getRequestBody(), request.getCharset());
    } else {
      entity = new UrlEncodedFormEntity(request.getNameValuePairList(), request.getCharset());
    }
    URI uri = builder.build();
    httpPost = new HttpPost(uri);
    httpPost.setEntity(entity);
    return httpPost;
  }

}
