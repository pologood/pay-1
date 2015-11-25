package com.sogou.pay.thirdpay.biz.utils;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/2 18:56
 */
public class WechatPayUtil {

    //微信银行类型
    public static String BANK_TYPE = "WX";
    //币种类别
    public static String FEE_TYPE = "CNY";
    public static final String TRADE_TYPE = "NATIVE";//扫码交易类型
    public static final String SDK_TRADE_TYPE = "APP";//SDK交易类型
    //编码类别
    public static String INPUT_CHARSET = "UTF-8";
    //微信开发平台应用id
    public static String APP_ID = "wx28ced6175e2ef6ca";
    //应用对应的凭证
    public static String APP_SECRET = "84dea587fe40196acb57734b1d564f46";
    //应用对应的密钥
    public static String
            APP_KEY =
            "tDxowKe2R8yqbkKvsm41S85E7gutn0DEKLibkB25BrweGs7A6vbSdb5haONDaEt7HoGzaM6Re7BPFrbH2kmXc5YFdh01G8imHwaQYai7bjiEAKIYL8OevJ2gs3UK6S5S";
    //常量固定值
    public static String GRANT_TYPE = "client_credential";
    //access_token失效后请求返回的errcode
    public static String EXPIRE_ERRCODE = "42001";
    //重复获取导致上一次获取的access_token失效,返回错误码
    public static String FAIL_ERRCODE = "40001";
    //access_token常量值
    public static String ACCESS_TOKEN = "access_token";
    //用来判断access_token是否失效的值
    public static String ERRORCODE = "errcode";
    //签名算法常量值
    public static String SIGN_METHOD = "sha1";
    public static int TIME_OUT = 5;

    //下载对账单网关
    public static final String DOWNLOAD_GATEWAY="https://api.mch.weixin.qq.com/pay/downloadbill";

    //下载字符集
    public static final String DOWNLOAD_INPUT_CHARSET = "GBK";

    //下载方法
    public static final String DOWNLOAD_METHOD = "GET";
}
