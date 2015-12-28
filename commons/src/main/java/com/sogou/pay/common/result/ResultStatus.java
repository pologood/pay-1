package com.sogou.pay.common.result;

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
    APPID_NOTEXISTED(1500, "应用ID不存在"),
    SIGNATURE_ERROR(1510, "签名错误"),
    //-------------------支付请求错误码--------2000至2999----------
    PARAM_ERROR(2000, "缺少必选参数或存在非法参数"),
    PAY_SING_ERROR(2001, "验证签名失败"),
    PAY_INSERT_PAY_ORDER_ERROR(2002, "插入支付单信息失败"),
    PAY_APP_NOT_EXIST(2003, "该业务平台不存在"),
    PAY_CHANNEL_NOT_EXIST(2004, "该支付渠道不存在"),
    PAY_CHANNEL_ADAPT_NOT_EXIST(2005, "收银台银行适配列表不存在"),
    PAY_BANK_ROUTER_NOT_EXIST(2006, "路由第三方不存在"),
    PAY_ORDER_PAY_SUCCESS(2007, "该笔支付单已经支付"),
    PAY_ORDER_NOT_EXIST(2008, "该笔支付单不存在"),
    PAY_ORDER_MONEY_ERROR(2009, "请求金额与支付单金额不符"),
    PAY_MERCHANT_NOT_EXIST(2010, "该支付机构商户不存在"),
    PAY_AGENCY_NOT_EXIST(2011, "该支付机构不存在"),
    PAY_SING_IS_NULL(2012, "签名为空！"),
    PAY_CHANNEL_IS_NULL(2013, "没有支付渠道！"),
    PAY_BANKID_IS_NULL(2014, "支付渠道为空！"),
    THIRD_PAY_ERROR(2599,"请求第三方失败"),
    THIRD_PAY_PARAM_ERROR(2500, "支付请求缺少必选参数或存在非法参数"),
    THIRD_PAY_SYSTEM_ERROR(2501, "支付请求系统错误"),
    THIRD_PAY_ALI_CLIENT_ENCODE_ERROR(2502, "对签名进行编码出错"),
    THIRD_PAY_WEHCHAT_HTTP_ERROR(2503, "请求微信支付HTTP异常"),
    THIRD_PAY_WEHCHAT_BACK_PARAM_ERROR(2504, "请求微信支付返回参数异常"),
    THIRD_PAY_SECRET_KEY_ERROR(2505, "获取支付平台第三方账户密钥异常"),
    THIRD_PAY_GET_ALIS_KEY_ERROR(2506, "获取支付宝公钥出现异常"),
    THIRD_PAY_ALI_SIGN_ERROR(2507, "请求支付宝支付参数签名错误"),
    THIRD_PAY_TEN_SIGN_ERROR(2508, "请求财付通支付参数签名错误"),
    THIRD_PAY_WECHAT_SIGN_ERROR(2509, "请求微信支付参数签名错误"),
    THIRD_PAY_GET_TEN_TOKENID_ERROR(2510, "获取财付通wap预支付id失败"),
    THIRD_PAY_CHANNEL_NOT_EXIST(2004, "该支付渠道不存在"),
    THIRD_PAY_HTTP_ERROR(2511, "请求财付通支付HTTP请求异常"),
    THIRD_PAY_ALI_HTTP_ERROR(2512, "支付宝Wap预支付HTTP请求异常"),
    THIRD_PAY_ALI_WAP_ENCODE_ERROR(2513, "支付宝Wap预支付对签名进行编码出错"),
    THIRD_PAY_ALI_WAP_BACK_ERROR(2514, "支付宝Wap预支付返回参数异常"),
    THIRD_PAY_ALI_DECRYPT_ERROR(2515, "支付宝Wap返回数据解密失败"),
    THIRD_GETACCESSTOKEN_ERROR(4102, "微信sdk支付获取token失败"),

    //-------------------支付回调错误码--------3000至3999-----------
    REPAIR_ORDER_ERROR(3000, "补单调用失败"),
    INSERT_RES_DETAIL_ERROR(3001, "插入回调流水错误"),
    RES_DETAIL_ALREADY_EXIST(3002, "回调流水已经存在"),
    THIRD_NOTIFY_SIGN_ERROR(3003, "回调签名错误"),
    THIRD_NOTIFY_PARAM_ERROR(3004, "回调参数异常"),
    RES_PAY_INFO_NOT_EXIST_ERROR(3005, "支付单信息不存在"),
    REQ_INFO_NOT_EXIST_ERROR(3006, "支付请求流水不存在"),
    //----------------------退款错误码--------4000至4999------------
    REFUND_PARAM_ERROR(4000, "缺少必选参数或存在非法参数"),
    REFUND_SIGN_ERROR(4010, "验证签名错误"),
    REFUND_SYSTEM_ERROR(4001, "退款请求系统错误"),
    REFUND_SERVICE_ERROR(4002, "退款请求业务处理错误"),
    REFUND_PARAM_MON_ERROR(4003, "退款金额与支付金额不符"),
    REFUND_UNEXIST_ORDER(4004, "不存在此订单信息或订单状态有误"),
    REFUND_ORDER_UNPAY(4011, "此订单未支付"),
    REFUND_EXIST_REFUNDID(4005, "已有退款单在执行中"),
    REFUND_QUERY_PAYRES_ERROR(4006, "退款查询支付回调异常"),
    REFUND_EXIST_REFUND_SUCCESS(4007, "此订单已经退款成功"),
    REFUND_QUERY_MERCHANT_ERROR(4008, "退款查询支付商户信息异常"),
    REFUND_QUERY_AGENCY_ERROR(4009, "退款查询支付机构信息异常"),
    THIRD_REFUND_NOTIFY_PARAM_ERROR(4200, "退款回调参数异常"),
    THIRD_REFUND_NOTIFY_SIGN_ERROR(4201, "退款回调签名错误"),
    THIRD_REFUND_NOTIFY_SERVICE_ERROR(4202, "退款回调服务处理错误"),
    THIRD_REFUND_ALI_SIGN_ERROR(4500, "支付宝退款请求签名错误"),
    THIRD_REFUND_ALI_HTTP_ERROR(4501, "支付宝退款请求HTTP异常"),
    THIRD_REFUND_ALI_BACK_PARAM_ERROR(4502, "支付宝退款返回参数异常"),
    THIRD_REFUND_TEN_SIGN_ERROR(4503, "财付通退款请求签名错误"),
    THIRD_REFUND_TEN_HTTP_ERROR(4504, "财付通退款请求HTTP异常"),
    THIRD_REFUND_TEN_BACK_PARAM_ERROR(4505, "财付通退款返回参数异常"),
    THIRD_REFUND_WECHAT_SIGN_ERROR(4506, "微信退款请求签名错误"),
    THIRD_REFUND_WECHAT_HTTP_ERROR(4507, "微信退款请求HTTP异常"),
    THIRD_REFUND_WECHAT_BACK_PARAM_ERROR(4508, "微信退款返回参数异常"),
    THIRD_REFUND_PARAM_ERROR(4509, "退款请求缺少必选参数或存在非法参数"),
    THIRD_REFUND_SYSTEM_ERROR(4510, "退款请求系统错误"),
    THIRD_REFUND_WECHAT_BACK_SIGN_ERROR(4511, "微信退款返回参数验证签名异常"),
    THIRD_REFUND_TEN_BACK_SIGN_ERROR(4512, "财付通退款返回参数验证签名异常"),
    THIRD_REFUND_ALI_BACK_SIGN_ERROR(4513, "支付宝退款返回参数验证签名异常"),
    THIRD_REFUND_WECHAT_XMLTOMAP_ERROR(4514, "微信退款请求返回XML参数转换Map异常"),
    THIRD_REFUND_TEN_XMLTOMAP_ERROR(4515, "财付通退款请求返回XML参数转换Map异常"),
    THIRD_REFUND_ALI_XMLTOMAP_ERROR(4516, "支付宝退款请求返回XML参数转换Map异常"),
    FAIL_ACC_REFUND_NOT_REQ_ERROR(4600, "平账退款查询不到支付回调记录"),
    FAIL_ACC_REFUND_NOT_PAYORDER_ERROR(4601, "平账退款查询不到支付订单记录"),
    FAIL_ACC_REFUND_NOT_MERCHANT_ERROR(4602, "平账退款查询不到支付商户记录"),
    FAIL_ACC_REFUND_NOT_AGENCY_ERROR(4603, "平账退款查询不到支付机构记录"),

    THIRD_REFUND_99BILL_SIGN_ERROR(4700, "快钱退款请求签名错误"),
    THIRD_REFUND_99BILL_HTTP_ERROR(4701, "快钱退款请求HTTP异常"),
    THIRD_REFUND_99BILL_BACK_PARAM_ERROR(4702, "快钱退款返回参数异常"),
    THIRD_REFUND_99BILL_BACK_SIGN_ERROR(4703, "快钱退款返回参数验证签名异常"),
    THIRD_REFUND_99BILL_XMLTOMAP_ERROR(4704, "快钱退款请求返回XML参数转换Map异常"),
    //----------------------对账错误码--------5000至5999------------
    INSERT_PAY_CHECK_WAITING_ERROR(5000, "插入在线支付流水对帐单表错误"),

    //----------------------查询订单错误码--------6000至6999------------
    THIRD_QUERY_PARAM_ERROR(6000, "缺少必选参数或存在非法参数"),
    THIRD_QUERY_SYSTEM_ERROR(6001, "查询请求请求系统错误"),
    THIRD_QUERY_ALI_HTTP_ERROR(6002, "请求支付宝查询订单信息HTTP异常"),
    THIRD_QUERY_TEN_SIGN_ERROR(6004, "请求财付通查询订单签名错误"),
    THIRD_QUERY_ALI_PAY_INFO_ERROR(6003, "请求支付宝查询订单信息返回参数异常"),
    THIRD_QUERY_TEN_PAY_INFO_ERROR(6005, "请求财付通查询订单信息返回参数异常"),
    THIRD_QUERY_WECHAT_SIGN_ERROR(6006, "请求微信查询订单签名错误"),
    THIRD_QUERY_WECHAT_PAY_INFO_ERROR(6007, "请求微信查询订单信息返回参数异常"),
    THIRD_QUERY_WECHAT_BACK_SIGN_ERROR(6008, "微信查询订单信息返回参数签名校验异常"),
    THIRD_QUERY_ALI_BACK_SIGN_ERROR(6009, "支付宝查询订单信息返回参数签名校验异常"),
    THIRD_QUERY_TEN_BACK_SIGN_ERROR(6010, "财付通查询订单信息返回参数签名校验异常"),
    THIRD_QUERY_TEN_HTTP_ERROR(6011, "请求财付通查询订单信息HTTP异常"),
    THIRD_QUERY_WECHAT_HTTP_ERROR(6012, "请求微信查询订单信息HTTP异常"),
    THIRD_QUERY_WECHAT_XMLTOMAP_ERROR(6013, "微信查询订单请求返回XML参数转换Map异常"),
    THIRD_QUERY_TEN_XMLTOMAP_ERROR(6014, "财付通查询订单请求返回XML参数转换Map异常"),
    THIRD_QUERY_ALI_XMLTOMAP_ERROR(6015, "支付宝查询订单请求返回XML参数转换Map异常"),
    THIRD_QUERY_ALI_SIGN_ERROR(6016, "请求支付宝查询订单签名错误"),
    PAY_ORDER_RELATION_NOT_EXIST(6017, "支付单关联信息不存在"),
    THIRD_QUERY_99BILL_SIGN_ERROR(6018, "请求快钱查询订单签名错误"),
    THIRD_QUERY_99BILL_PAY_INFO_ERROR(6019, "请求快钱查询订单信息返回参数异常"),
    THIRD_QUERY_99BILL_XMLTOMAP_ERROR(6020, "快钱查询订单请求返回XML参数转换Map异常"),
    THIRD_QUERY_99BILL_HTTP_ERROR(6021, "请求快钱查询订单信息HTTP异常"),
    QUERY_ORDER_PARAM_ERROR(6022,"缺少必选参数或存在非法参数"),
    QUERY_ORDER_SIGN_ERROR(6023, "验证签名错误"),
    QUERY_ORDER_APP_NOT_EXIST(6024, "该业务平台不存在"),

    //----------------------查询订单退款错误码--------6500至6599------------
    THIRD_Q_RF_PARAM_ERROR(6500, "查询订单退款请求缺少必选参数或存在非法参数"),
    THIRD_Q_RF_SYSTEM_ERROR(6501, "查询订单退款请求请求系统错误"),
    THIRD_Q_RF_ALI_HTTP_ERROR(6502, "请求支付宝查询订单退款HTTP异常"),
    THIRD_Q_RF_ALI_PAY_INFO_ERROR(6503, "请求支付宝查询订单退款返回参数异常"),
    THIRD_Q_RF_TEN_SIGN_ERROR(6504, "请求财付通查询订单退款签名错误"),
    THIRD_Q_RF_TEN_PAY_INFO_ERROR(6505, "请求财付通查询订单退款返回参数异常"),
    THIRD_Q_RF_WECHAT_SIGN_ERROR(6506, "请求微信查询订单退款签名错误"),
    THIRD_Q_RF_WECHAT_PAY_INFO_ERROR(6507, "请求微信查询订单退款返回参数异常"),
    THIRD_Q_RF_WECHAT_BACK_SIGN_ERROR(6508, "微信查询订单退款返回参数签名校验异常"),
    THIRD_Q_RF_ALI_BACK_SIGN_ERROR(6509, "支付宝查询订单退款返回参数签名校验异常"),
    THIRD_Q_RF_TEN_BACK_SIGN_ERROR(6510, "财付通查询订单退款返回参数签名校验异常"),
    THIRD_Q_RF_TEN_HTTP_ERROR(6511, "请求财付通查询订单退款HTTP异常"),
    THIRD_Q_RF_WECHAT_HTTP_ERROR(6512, "请求微信查询订单退款HTTP异常"),
    THIRD_Q_RF_WECHAT_XMLTOMAP_ERROR(6513, "微信查询订单退款请求返回XML参数转换Map异常"),
    THIRD_Q_RF_TEN_XMLTOMAP_ERROR(6514, "财付通查询订单退款请求返回XML参数转换Map异常"),
    THIRD_Q_RF_ALI_XMLTOMAP_ERROR(6515, "支付宝查询订单退款请求返回XML参数转换Map异常"),
    THIRD_Q_RF_ALI_SIGN_ERROR(6516, "请求支付宝查询订单退款签名错误"),
    Q_RF_ORDERNOTREFUND_ERROR(6517, "此订单不存在退款记录"),
    Q_RF_ORDERID_ERROR(6518, "此订单不存在"),

    THIRD_Q_RF_99BILL_HTTP_ERROR(6502, "请求快钱查询订单退款HTTP异常"),
    THIRD_Q_RF_99BILL_PAY_INFO_ERROR(6503, "请求快钱查询订单退款返回参数异常"),
    THIRD_Q_RF_99BILL_BACK_SIGN_ERROR(6509, "快钱查询订单退款返回参数签名校验异常"),
    THIRD_Q_RF_99BILL_XMLTOMAP_ERROR(6515, "快钱查询订单退款请求返回XML参数转换Map异常"),
    THIRD_Q_RF_99BILL_SIGN_ERROR(6516, "请求快钱查询订单退款签名错误"),

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
