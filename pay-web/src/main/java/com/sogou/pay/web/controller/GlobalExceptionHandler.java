package com.sogou.pay.web.controller;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Throwables;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public String handle(Exception e) {
    email(e);
    return e.getMessage();
  }

  private RestTemplate restTemplateGBK = new RestTemplate(httpComponentsClientHttpRequestFactory());

  @Autowired
  public void init() {
    Charset gbk = Charset.forName("GBK");
    FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
    formHttpMessageConverter.setCharset(gbk);
    restTemplateGBK.getMessageConverters().add(0, formHttpMessageConverter);
    restTemplateGBK.getMessageConverters().add(1, new StringHttpMessageConverter(gbk));
  }

  private ClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(60));
    factory.setReadTimeout((int) TimeUnit.SECONDS.toMillis(60));
    return factory;
  }

  private static final String EMAIL_LIST = "wangwenlong@sogou-inc.com",
      EMAIL_URL = "http://mail.portal.sogou/portal/tools/send_mail.php";

  public void email(Exception e) {
    restTemplateGBK.postForObject(EMAIL_URL, getEmail(e), Map.class);
  }

  private MultiValueMap<String, String> getEmail(Exception e) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("uid", "pay@sogou-inc.com");
    map.add("fr_name", "Sogou Pay");
    map.add("fr_addr", "pay@sogou-inc.com");
    map.add("mode", "text");
    map.add("title", "支付异常");
    map.add("maillist", EMAIL_LIST);
    map.add("body", getBody(e));
    return map;
  }

  private String getBody(Exception e) {
    return new StringBuilder(LocalDateTime.now().toString()).append(" ").append(Throwables.getStackTraceAsString(e))
        .toString();
  }

}
