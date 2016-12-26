package com.sogou.pay.common.types;

import java.util.Objects;

public enum ResultStatus {
    SUCCESS(0, "成功"),
        
    SYSTEM_ERROR(1000, "系统错误"),
    SYSTEM_DB_ERROR(1001, "数据库错误"),
    PARAM_ERROR(2000, "缺少必选参数或存在非法参数"),
    SIGN_ERROR(2001, "签名失败"),
    VERIFY_SIGN_ERROR(2001, "验证签名失败"),
    INTERFACE_NOT_IMPLEMENTED(1003, "接口未实现"),

    APPID_NOT_EXIST(2003, "业务线不存在"),
    PAY_CHANNEL_NOT_EXIST(2004, "支付渠道不存在"),
    PAY_CHANNEL_ADAPT_NOT_EXIST(2005, "收银台渠道适配列表不存在"),
    BANK_ROUTER_NOT_EXIST(2006, "银行路由不存在"),
    THIRD_MERCHANT_NOT_EXIST(2010, "第三方支付机构商户不存在"),
    THIRD_AGENCY_NOT_EXIST(2011, "第三方支付机构不存在"),
    ORDER_ALREADY_DONE(2007, "支付单已支付"),
    ORDER_NOT_EXIST(2008, "支付单不存在"),
    ORDER_ALREADY_EXIST(7106, "支付单已存在"),
    ORDER_NOT_PAY(4006, "支付单未支付"),
    ORDER_FAILED(4006, "支付单支付失败"),
    ORDER_MONEY_ERROR(2009, "支付单金额错误"),
    ORDER_REPAIRE_FAILED(3002, "补单调用失败"),
    ORDER_RELATION_NOT_EXIST(2012, "支付单支付请求关联信息不存在"),
    RES_DETAIL_ALREADY_EXIST(3004, "支付回调流水已存在"),
    RES_DETAIL_NOT_EXIST(3005, "支付回调流水不存在"),
    REQ_DETAIL_NOT_EXIST(3006, "支付请求流水不存在"),
    HANDLE_THIRD_NOTIFY_ERROR(3007, "处理第三方回调失败"),

    THIRD_ERROR(1100,"第三方请求失败"),
    THIRD_PAY_CHANNEL_NOT_EXIST(2021, "第三方支付渠道不存在"),
    THIRD_PARAM_ERROR(1101, "第三方请求缺少必选参数或存在非法参数"),
    THIRD_SIGN_ERROR(1102, "第三方请求签名失败"),
    THIRD_HTTP_ERROR(1103, "第三方请求发起HTTP请求失败"),
    THIRD_RESPONSE_PARAM_ERROR(1104, "第三方请求返回参数异常"),
    THIRD_NOTIFY_PARAM_ERROR(1104, "第三方请求返回参数异常"),
    THIRD_VERIFY_SIGN_ERROR(1105, "第三方请求返回签名错误"),
    THIRD_GET_KEY_ERROR(2020, "获取第三方密钥异常"),

    PARTIAL_REFUND_NOT_ALLOWED(4004, "不支持部分退款"),
    REFUND_NOT_EXIST(4005, "退款单不存在"),
    REFUND_PROCESSING(4007, "已有退款单在执行中"),
    REFUND_ALREADY_DONE(4008, "退款单已退款成功"),
    REFUND_FAILED(4007, "退款单退款失败"),

    SAVE_BILL_FAILED(5001, "保存支付流水对帐单错误");

    private int code;
    private String message;

    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    /**
     * 状态信息
     */
    public String getMessage() {
        return message;
    } 
   
    public static boolean isError(ResultStatus status) {
      return Objects.isNull(status) || !Objects.equals(status.code, SUCCESS.code);
    }
}
