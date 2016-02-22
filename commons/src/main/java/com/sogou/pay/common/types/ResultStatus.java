package com.sogou.pay.common.types;

/**
 * Created by hujunfei Date: 15-1-5 Time: 下午12:30 <ul> <li>1000-1499: 系统错误</li> <li>1500-1999:
 * 安全错误：参数验证、签名、参数非法、应用信息非法</li> <li>2000-2999: 支付请求错误</li> <li>3000-3999: 支付回调错误</li>
 * <li>4000-4999: 退款错误</li> <li>5000-5999: 对账错误</li>
 * <p/>
 * <li>8000-8999: 依赖平台错误</li> </ul>
 */
public enum ResultStatus {
    SUCCESS(0, "成功"),

    // ------------------系统错误-----------------------
    SYSTEM_ERROR(1000, "系统错误"),
    SYSTEM_DB_ERROR(1001, "数据库系统错误"),
    SIGNATURE_ERROR(1510, "签名错误"),

    //-------------------支付请求错误码--------2000至2999----------
    PAY_PARAM_ERROR(2000, "缺少必选参数或存在非法参数"),
    PAY_SIGN_ERROR(2001, "验证签名失败"),
    PAY_INSERT_PAY_ORDER_ERROR(2002, "插入支付单信息失败"),
    PAY_APP_NOT_EXIST(2003, "该业务平台不存在"),
    PAY_CHANNEL_NOT_EXIST(2004, "该支付渠道不存在"),
    PAY_CHANNEL_ADAPT_NOT_EXIST(2005, "收银台银行适配列表不存在"),
    PAY_BANK_ROUTER_NOT_EXIST(2006, "路由第三方不存在"),
    PAY_ORDER_ALREADY_DONE(2007, "该笔支付单已经支付"),
    PAY_ORDER_NOT_EXIST(2008, "该笔支付单不存在"),
    PAY_ORDER_MONEY_ERROR(2009, "请求金额与支付单金额不符"),
    PAY_MERCHANT_NOT_EXIST(2010, "该支付机构商户不存在"),
    PAY_AGENCY_NOT_EXIST(2011, "该支付机构不存在"),
    PAY_ORDER_RELATION_NOT_EXIST(6017, "支付单关联信息不存在"),
    THIRD_PAY_ERROR(2599,"请求第三方失败"),
    THIRD_PAY_PARAM_ERROR(2500, "支付请求缺少必选参数或存在非法参数"),
    THIRD_PAY_SIGN_ERROR(2511, "请求第三方支付时签名失败"),
    THIRD_PAY_HTTP_ERROR(2511, "请求第三方支付时发起HTTP请求失败"),
    THIRD_PAY_RESPONSE_PARAM_ERROR(2504, "请求第三方支付时返回参数异常"),
    THIRD_PAY_RESPONSE_SIGN_ERROR(4512, "第三方退款返回签名错误"),
    THIRD_PAY_XML_PARSE_ERROR(4514, "第三方退款解析响应报文异常"),
    THIRD_PAY_GET_KEY_ERROR(2505, "获取第三方支付账户密钥异常"),
    THIRD_PAY_CHANNEL_NOT_EXIST(2004, "该支付渠道不存在"),

    //-------------------支付回调错误码--------3000至3999-----------
    THIRD_NOTIFY_SIGN_ERROR(3003, "回调签名错误"),
    THIRD_NOTIFY_PARAM_ERROR(3004, "回调参数异常"),
    REPAIR_ORDER_ERROR(3000, "补单调用失败"),
    INSERT_RES_DETAIL_ERROR(3001, "插入回调流水错误"),
    RES_DETAIL_ALREADY_EXIST(3002, "回调流水已经存在"),
    REQ_INFO_NOT_EXIST_ERROR(3006, "支付请求流水不存在"),

    //----------------------退款错误码--------4000至4999------------
    REFUND_SYSTEM_ERROR(4001, "退款时系统错误"),
    REFUND_DB_ERROR(4001, "写数据库失败"),
    REFUND_PARAM_ERROR(4000, "退款请求缺少必选参数或存在非法参数"),
    REFUND_SIGN_ERROR(4010, "退款请求签名错误"),
    REFUND_PARTIAL_REFUND(4003, "退款金额与支付金额不符"),
    REFUND_ORDER_NOT_EXIST(4004, "不存在此订单信息或订单状态有误"),
    REFUND_ORDER_NOT_PAY(4011, "此订单未支付"),
    REFUND_REFUND_PROCESSING(4005, "已有退款单在执行中"),
    REFUND_REFUND_ALREADY_DONE(4007, "此订单已经退款成功"),

    THIRD_REFUND_NOTIFY_PARAM_ERROR(4200, "第三方退款回调参数异常"),
    THIRD_REFUND_NOTIFY_SIGN_ERROR(4201, "第三方退款回调签名错误"),
    THIRD_REFUND_NOTIFY_ERROR(4202, "第三方退款回调错误"),

    THIRD_REFUND_ERROR(4510, "第三方退款失败"),
    THIRD_REFUND_PARAM_ERROR(4509, "第三方退款请求缺少必选参数或存在非法参数"),
    THIRD_REFUND_SIGN_ERROR(4500, "第三方退款请求签名失败"),
    THIRD_REFUND_HTTP_ERROR(4501, "第三方退款请求HTTP异常"),
    THIRD_REFUND_RESPONSE_PARAM_ERROR(4502, "第三方退款返回参数异常"),
    THIRD_REFUND_RESPONSE_SIGN_ERROR(4512, "第三方退款返回签名错误"),
    THIRD_REFUND_XML_PARSE_ERROR(4514, "第三方退款解析响应报文异常"),

    FAIL_ACC_REFUND_NOT_REQ_ERROR(4600, "平账退款查询不到支付回调记录"),
    FAIL_ACC_REFUND_NOT_PAYORDER_ERROR(4601, "平账退款查询不到支付订单记录"),
    FAIL_ACC_REFUND_NOT_MERCHANT_ERROR(4602, "平账退款查询不到支付商户记录"),
    FAIL_ACC_REFUND_NOT_AGENCY_ERROR(4603, "平账退款查询不到支付机构记录"),

    //----------------------对账错误码--------5000至5999------------
    INSERT_PAY_CHECK_WAITING_ERROR(5000, "插入在线支付流水对帐单表错误"),

    //----------------------查询订单错误码--------6000至6999------------
    QUERY_ORDER_PARAM_ERROR(6022,"订单查询缺少必选参数或存在非法参数"),
    QUERY_ORDER_SIGN_ERROR(6023, "订单查询签名错误"),
    THIRD_QUERY_ERROR(6001, "第三方订单查询失败"),
    THIRD_QUERY_PARAM_ERROR(6000, "第三方订单查询缺少必选参数或存在非法参数"),
    THIRD_QUERY_SIGN_ERROR(6004, "第三方订单查询签名失败"),
    THIRD_QUERY_HTTP_ERROR(6002, "第三方订单查询HTTP异常"),
    THIRD_QUERY_RESPONSE_PARAM_ERROR(6003, "第三方订单查询返回参数异常"),
    THIRD_QUERY_RESPONSE_SIGN_ERROR(6009, "第三方订单查询返回签名错误"),
    THIRD_QUERY_XML_PARSE_ERROR(6014, "第三方订单查询解析响应报文异常"),

    //----------------------查询退款订单错误码--------6500至6599------------
    QUERY_REFUND_REFUND_NOT_EXIST(6517, "此订单不存在退款记录"),
    QUERY_REFUND_ORDER_NOT_EXIST(6518, "此订单不存在"),
    THIRD_QUERY_REFUND_ERROR(6501, "第三方支付查询订单退款系统错误"),
    THIRD_QUERY_REFUND_PARAM_ERROR(6500, "查询订单退款缺少必选参数或存在非法参数"),
    THIRD_QUERY_REFUND_SIGN_ERROR(6504, "请求财付通查询订单退款签名错误"),
    THIRD_QUERY_REFUND_HTTP_ERROR(6502, "请求支付宝查询订单退款HTTP异常"),
    THIRD_QUERY_REFUND_RESPONSE_PARAM_ERROR(6503, "请求支付宝查询订单退款返回参数异常"),
    THIRD_QUERY_REFUND_RESPONSE_SIGN_ERROR(6510, "财付通查询订单退款返回参数签名校验异常"),
    THIRD_QUERY_REFUND_XML_PARSE_ERROR(6515, "支付宝查询订单退款请求返回XML参数转换Map异常"),

    //----------------------查询订单退款错误码--------7000至7100------------
    BANK_PAY_PARAM_ERROR(7000, "批量银行代付接口请求缺少必选参数或存在非法参数"),
    BANK_PAY_GET_PARAM_ERROR(7001, "批量银行代付接口请求组装参数异常"),
    BANK_PAY_HTTP_ERROR(7002, "批量银行代付接口请求HTTP异常"),
    BANK_PAY_BACK_PARAM_ERROR(7003, "批量银行代付接口返回参数异常"),
    BANK_PAY_SIGN_ERROR(7008, "批量银行代付接口MD5签名错误"),
    BANK_PAY_QUERY_PARAM_ERROR(7004, "批量银行代付查询接口请求缺少必选参数或存在非法参数"),
    BANK_PAY_QUERY_GET_PARAM_ERROR(7005, "批量银行代付查询接口请求组装参数异常"),
    BANK_PAY_QUERY_HTTP_ERROR(7006, "批量银行代付查询接口请求HTTP异常"),
    BANK_PAY_QUERY_BACK_PARAM_ERROR(7007, "批量银行代付查询接口返回参数异常"),
    BANK_PAY_QUERY_SIGN_ERROR(7009, "批量银行代付查询接口MD5签名错误"),
    BANK_REFUND_QUERY_GET_PARAM_ERROR(7010, "退票查询接口请求组装参数异常"),
    BANK_REFUND_QUERY_HTTP_ERROR(7011, "退票查询接口请求HTTP异常"),
    BANK_REFUND_QUERY_BACK_PARAM_ERROR(7012, "退票查询接口返回参数异常"),
    BANK_REFUND_SIGN_ERROR(7013, "退票查询接口MD5签名错误"),

    //----------------------代付单业务码--------7100至7200------------
    PAY_TRANFER_BATCH_NOT_EXIST(8001, "代付单批次不存在"),
    PAY_TRANFER_BATCH_STATUS_NOT_AUDIT_PASS(8002, "代付单批次审核状态不是通过状态"),
    PAY_TRANFER_BATCH_REPEAT_SUBMITTED(8003, "该批次单已经提交到银行，请勿重复提交"),
    PAY_TRANFER_NOT_EXIST(8004, "代付单不存在"),
    PAY_TRANFER_REQUEST_ERROR(8005, "代付请求错误"),
    PAY_TRANFER_RESPONSE_MESSAGE_PARSING_FAILED(8006, "代付解析xml错误"),
    REPEAT_ORDER_ERROR(8007, "提交代付单中有相同的代付单号"),
    DB_REPEAT_ORDER_ERROR(8008, "代付单号重复"),
    DB_REPEAT_BATCHNO_ERROR(8009, "代付批次号重复"),
    PAY_TRANFER_REQUEST_TIME_OUT(8010, "代付请求超时"),
    PAY_TRANFER_REQUEST_CONNECT_ERROR(8011, "代付请求前置机异常"),
    ;

    private int code;
    private String message;

    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 状态信息
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 状态代码字符串
     */
    public String getName() {
        return this.name();
    }

    /**
     * 向外部输出的状态代码字符串
     */
    public String getOutputName() {
        return this.name();
    }

    @Override
    public String toString() {
        return getName();
    }
}
