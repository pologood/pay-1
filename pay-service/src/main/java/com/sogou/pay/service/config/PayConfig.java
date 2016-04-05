package com.sogou.pay.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by qibaichao on 2015/6/17.
 * 支付常量
 */

public class PayConfig {

//    //代发登录用户
//    @Value("${pay.transfer.lgnName}")
//    public static String payTransferLgnName = "";
//    //代发连接地址
//    @Value("${pay.transfer.host}")
//    public static String payTransferHost = "localhost";
//    //代发端口
//    @Value("${pay.tranfer.port}")
//    public static int payTranferPort = 1080;
//    //通知url
//    public static String payTransferNotifyUrl = "";


  //代发登录用户
  public static String payTransferLgnName = "dmcp1";
  //代发连接地址
  public static String payTransferHost = "http://localhost:8088";
  //通知url
  public static String payTransferNotifyUrl = "";
  //主机时间
  public static String payTransferQueryDate = "";

  public static String mailServiceUrl = "";

  public static String mailServiceUid = "";

  public static String mailServiceUname = "";

  static {
    try {
      Properties prop = new Properties();
      InputStream in = PayConfig.class.getResourceAsStream("/config.properties");
      prop.load(in);
      payTransferLgnName = prop.getProperty("pay.transfer.lgnName").trim();
      payTransferHost = prop.getProperty("pay.transfer.host").trim();
      payTransferQueryDate = prop.getProperty("pay.transfer.query.date").trim();
      mailServiceUrl = prop.getProperty("mail.service.url").trim();
      mailServiceUid = prop.getProperty("mail.service.uid").trim();
      mailServiceUname = prop.getProperty("mail.service.uname").trim();
      payTransferNotifyUrl = prop.getProperty("pay.transfer.notify.url").trim();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
