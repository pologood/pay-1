package com.sogou.pay.thirdpay.biz.utils;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/2 18:32
 */
public class AliPayUtil {

    /**
     * 支付宝网关、账户、扫码支付所需共同参数
     */
    public static final String ALI_ACCOUNT_SERVICE = "create_direct_pay_by_user"; //支付宝账户支付接口名
    public static final String INPUT_CHARSET = "utf-8";                           // 字符编码格式 utf-8
    public static final String SIGN_TYPE = "MD5";                                 //签名方式
    public static final String PAYMENT_TYPE = "1";                                //支付类型
    /**
     * 支付宝查询订单参数
     */
    public static final String ALI_QUERY_SERVICE = "single_trade_query"; //支付宝查询订单接口名
    public static int TIME_OUT = 5;
    public final static String CHECK_URL = "https://mapi.alipay.com/gateway.do";//对账网关
    public final static String ACCOUNT_PAGE_QUERY_SERVCICE = "account.page.query";//财务明细分页查询接口
    /**
     * 支付宝查询订单退款参数
     */
    public static final String ALI_QUERY_REFUND_SERVICE = "refund_fastpay_query";
    /**
     * 支付宝扫码支付参数
     */
    // 1） 简约前置模式：qr_pay_mode=0;
    // 2） 前置模式：qr_pay_mode=1;
    // 3） 页面跳转模式：这个参数的值 qr_pay_mode=2 ，直接进入到支付宝收银台
    public static String QR_PAY_MODE = "0";   //扫码支付模式

    /**
     * 支付宝订单退款参数
     */
    public static final String ALI_REFUND_SERVICE = "refund_fastpay_by_platform_nopwd"; //支付宝订单退款接口名
    public static final String BATCH_NUM = "1"; //退款笔数

    /**
     * 支付宝钱包支付，调用支付宝接口名
     */
    // 支付宝钱包支付，超时时间设置 ，默认30分钟，一旦超时，该笔交易就会自动被关闭，取值范围：1m～15d。
    // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
    // 该参数数值不接受小数点，如1.5h，可转换为90m。
    public static final String IT_B_PAY = "30m";
    public static final String SERVICE = "mobile.securitypay.pay";
    public static String getSignType() {
        return "sign_type=\"RSA\"";
    }

    /**
     * 支付宝Wap支付参数
     */
    public static final String WAP_SERVICE = "alipay.wap.trade.create.direct";
    public static final String WAP_PAY_SERVICE = "alipay.wap.auth.authAndExecute";
    public static final String WAP_FORMAT = "xml";
    public static final String WAP_V = "2.0";
}
