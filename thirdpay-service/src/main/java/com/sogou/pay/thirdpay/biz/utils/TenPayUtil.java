package com.sogou.pay.thirdpay.biz.utils;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/1/22 11:09
 */
public class TenPayUtil {

    /**
     * 财付通账户支付参数
     */
    public static final String FEE_TYPE = "1";       //币种
    public static final String INPUT_CHARSET = "UTF-8";    // 字符编码格式
    public static final String ACCOUNT_BANK_TYPE = "DEFAULT";
    public static final String WEB_SIGN_TYPE = "MD5";  // 签名方式 不需修改
    public static int TIME_OUT = 5;
    public static final String VER = "2.0";          //版本号，暂定为2.0
    public static final String SALE_PLAT = "211";    //请求来源
    public static final String WAP_CHARSET = "1";    //字符编码格式:1 ：UTF-8, 2 ：GB2312, 默认为 1
    public static final String BANK_TYPE = "0";      //银行类型
    //退款用到
    public static final String WL_OP_USER_ID = "1234274801";//搜狗网络商户账号
    public static final String KJ_OP_USER_ID = "1234639901";//搜狗科技商户账号
    public static final String WL_OP_USER_PASSWD = "3edcvfr4"; // 搜狗网络商户账号密码
    public static final String KJ_OP_USER_PASSWD = "3edcvfr4"; // 搜狗科技商户账号密码
    public static final String WL_CERT_PASSWD = "535221"; // 搜狗网络商户证书导入密码
    public static final String KJ_CERT_PASSWD = "446163"; // 搜狗科技商户证书导入密码
    //第三方支付提供的md5加密密钥
    public static final String SIGN_NAME = "sign"; // 签名字段


    /**
     * 下载对账网关
     */
    public static final String DOWNLOAD_GATEWAY = "http://mch.tenpay.com/cgi-bin/mchdown_real_new.cgi";

    /**
     * 下载字符集
     */
    public static final String DOWNLOAD_INPUT_CHARSET = "utf-8";

    /**
     * 下载方法
     */
    public static final String DOWNLOAD_METHOD = "GET";


}
