package com.sogou.pay.thirdpay.biz.utils.billpay;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/06/16 18:32
 */
public class BillPayUtil {

    /**
     * 快钱支付通用参数
     */
    //编码方式，1代表 UTF-8; 2 代表 GBK; 3代表 GB2312 默认为1,该参数必填。
    public static final String inputCharset = "1";
    //网关版本，固定值：v2.0,该参数必填
    public static final String version = "v2.0";
    //语言种类，1代表中文显示，2代表英文显示。默认为1,该参数必填。
    public static final String language = "1";
    //签名类型,该值为4，代表PKI加密方式,该参数必填。
    public static final String signType = "4";
    public static final String payType_acc = "12";
    public static final String payType_b2b = "14";
    /**
     * 快钱订单查询通用参数
     */
    //签名方式：1 代表 MD5 加密签名方式 2 代表 PKI 加密方式
    public static final String querySignType = "1";
    //查询方式：0 按商户订单号单笔查询（只返回成功订单） 1 按交易结束时间批量查询（只返回成功订单）
    public static final String queryType = "0";
    //查询方式:1 代表简单查询（返回基本订单信息）
    public static final String queryMode = "1";
    /**
     * 快钱退款通用参数
     */
    //退款接口版本号
    public static final String refund_version = "bill_drawback_api_1";
    public static final String command_type = "001";
}
