package com.sogou.pay.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by qibaichao on 2015/6/17.
 * 支付常量
 */

public class PayConfig {

//    //代发登录用户
//    @Value("${pay.tranfer.lgnName}")
//    public static String payTranferLgnName = "";
//    //代发连接地址
//    @Value("${pay.tranfer.host}")
//    public static String payTranferHost = "localhost";
//    //代发端口
//    @Value("${pay.tranfer.port}")
//    public static int payTranferPort = 1080;
//    //通知url
//    public static String payTranferNotifyUrl = "";


    //代发登录用户
    public static String payTranferLgnName = "dmcp1";
    //代发连接地址
    public static String payTranferHost = "http://localhost:8088";
    //通知url
    public static String payTranferNotifyUrl = "";

    static {
        try {
            Properties prop = new Properties();
            InputStream in = PayConfig.class.getResourceAsStream("/config.properties");
            prop.load(in);
            payTranferLgnName = prop.getProperty("pay.tranfer.lgnName").trim();
            payTranferHost = prop.getProperty("pay.tranfer.host").trim();
//            payTranferNotifyUrl = prop.getProperty("pay.tranfer.notify.url").trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
