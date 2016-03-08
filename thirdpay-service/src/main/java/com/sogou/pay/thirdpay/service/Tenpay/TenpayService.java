package com.sogou.pay.thirdpay.service.Tenpay;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.utils.*;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.common.enums.OrderRefundStatus;
import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.thirdpay.biz.model.OutCheckRecord;
//import com.sogou.pay.thirdpay.biz.model.TenpayCheckResponse;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.service.ThirdpayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by xiepeidong on 2016/1/19.
 */
@Service
public class TenpayService implements ThirdpayService {
    private static final Logger log = LoggerFactory.getLogger(TenpayService.class);

    /**
     * 财付通账户支付参数
     */
    public static final String FEE_TYPE = "1";       //币种
    public static final String INPUT_CHARSET = "UTF-8";    // 字符编码格式
    public static final String ACCOUNT_BANK_TYPE = "DEFAULT";
    public static final String SIGN_TYPE = "MD5";  // 签名方式 不需修改
    public static int TIME_OUT = 5;
    public static final String SALE_PLAT = "211";    //请求来源
    public static final String WAP_CHARSET = "1";    //字符编码格式:1 :UTF-8, 2 :GB2312, 默认为 1
    public static final String BANK_TYPE = "0";      //银行类型

    private static HashMap<String, String> TRADE_STATUS = new HashMap<String, String>();
    private static HashMap<String, String> REFUND_STATUS = new HashMap<String, String>();
    private static HashMap<String, String[]> REFUND_OPUSER = new HashMap<String, String[]>();//退款用到

    static {
        TRADE_STATUS.put("0", OrderStatus.SUCCESS.name());//等待卖家收款
        TRADE_STATUS.put("1", OrderStatus.USERPAYING.name());//买家支付中, 暂勿发货
        TRADE_STATUS.put("DEFAULT", OrderStatus.FAILURE.name());//默认
        //refund_status为4、10, 代表退款成功（最终态）, 资金已返回买家银行卡或者财付通账号。
        REFUND_STATUS.put("4", OrderRefundStatus.SUCCESS.name());
        REFUND_STATUS.put("10", OrderRefundStatus.SUCCESS.name());
        //refund_status为8、9、11, 代表退款处理中（中间态）, 9代表财付通已经提交退款请求给银行
        // （资金已从商户号中扣减, 退款记录会出现在对账单中）；
        REFUND_STATUS.put("8", OrderRefundStatus.PROCESSING.name());
        REFUND_STATUS.put("9", OrderRefundStatus.PROCESSING.name());
        REFUND_STATUS.put("11", OrderRefundStatus.PROCESSING.name());
        //refund_status为1、2, 代表状态未确定（中间态）, 需要商户使用原退款单号重新发起退款
        REFUND_STATUS.put("1", OrderRefundStatus.PROCESSING.name());
        REFUND_STATUS.put("2", OrderRefundStatus.PROCESSING.name());
        //refund_status为3、5、6, 代表失败（最终态）, 需要商户更换退款单号重新发起退款
        REFUND_STATUS.put("3", OrderRefundStatus.FAIL.name());
        REFUND_STATUS.put("5", OrderRefundStatus.FAIL.name());
        REFUND_STATUS.put("6", OrderRefundStatus.FAIL.name());
        //refund_status为7, 代表退款到银行发现用户的卡作废或者冻结了, 导致原路退款银行卡失败, 
        // 资金回流到商户的现金帐号, 需要商户人工干预, 通过线下或者财付通转账的方式进行退款。
        REFUND_STATUS.put("7", OrderRefundStatus.OFFLINE.name());
        REFUND_STATUS.put("DEFAULT", OrderRefundStatus.UNKNOWN.name());//默认
        REFUND_OPUSER.put("1234274801", new String[]{"1234274801123", "1234567809ted", "535221"});//搜狗网络商户账号、操作员账号、密码、证书导入密码
        REFUND_OPUSER.put("1234639901", new String[]{"1234639901123", "1234567809ted", "145404"});//搜狗科技商户账号、操作员账号、密码、证书导入密码
    }

    private ResultMap preparePayInfo(PMap params, String bankCode) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap requestPMap = new PMap();
        //1.获取通用固定参数
        requestPMap.put("fee_type", TenpayService.FEE_TYPE);                    // 币种:1-人民币
        requestPMap.put("input_charset", TenpayService.INPUT_CHARSET);          //编码格式
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));  //异步回调地址
        requestPMap.put("return_url", params.getString("pageNotifyUrl"));    //页面回调地址
        requestPMap.put("partner", params.getString("merchantNo"));          //商户号
        //2.获取财付通账户支付银行类型值
        requestPMap.put("bank_type", bankCode);
        //3.组装其他可变参数
        requestPMap.put("spbill_create_ip", params.getString("buyerIp"));    // 买家浏览器IP
        String orderAmount = TenpayUtils.fenParseFromYuan(params.getString("orderAmount"));
        requestPMap.put("total_fee", orderAmount);                           // 订单总金额, 以分为单位, 整数
        requestPMap.put("out_trade_no", params.getString("serialNumber"));   //订单id
        requestPMap.put("body", params.getString("subject"));                //商品描述
        requestPMap.put("sign_type", TenpayService.SIGN_TYPE);              //加密方法
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[preparePayInfo] 财付通订单支付参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_PARAM_ERROR);
        }
        //5.获取md5签名
        String md5securityKey = params.getString("md5securityKey");
        String
                sign = SecretKeyUtil.tenMD5Sign(requestPMap, md5securityKey, TenpayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[preparePayInfo] 财付通订单支付签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        //6.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;

    }

    @Override
    public ResultMap preparePayInfoAccount(PMap params) throws ServiceException {
        String bankcode = TenpayService.ACCOUNT_BANK_TYPE;//财付通账户支付银行类型字段为固定值:DEFAULT
        return preparePayInfo(params, bankcode);
    }

    @Override
    public ResultMap preparePayInfoGatway(PMap params) throws ServiceException {
        String bankcode = (String) params.get("bankCode");//财付通网关支付银行类型值
        return preparePayInfo(params, bankcode);
    }

    @Override
    public ResultMap preparePayInfoQRCode(PMap params) throws ServiceException {
        return null;
    }

    private ResultMap preparePayInfoMobile(PMap params, String callback_url) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装签名参数
        PMap requestPMap = new PMap();
        requestPMap.put("ver", "2.0");                           //接口版本
        requestPMap.put("charset", TenpayService.WAP_CHARSET);               //编码
        requestPMap.put("bank_type", TenpayService.BANK_TYPE);               //银行类型
        requestPMap.put("desc", params.getString("subject"));             //商品描述
        requestPMap.put("bargainor_id", params.getString("merchantNo"));
        requestPMap.put("sp_billno", params.getString("serialNumber"));
        String orderAmount = TenpayUtils.fenParseFromYuan(params.getString("orderAmount"));
        requestPMap.put("total_fee", orderAmount);                        // 订单总金额, 以分为单位, 整数
        requestPMap.put("fee_type", TenpayService.FEE_TYPE);                 //币种
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));
        if (callback_url != null)
            requestPMap.put("callback_url", callback_url);
        requestPMap.put("attach", params.getString("subject"));
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[preparePayInfoMobile] 财付通订单支付参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_PARAM_ERROR);
        }
        //2.获取md5签名
        String sign = SecretKeyUtil
                .tenMD5Sign(requestPMap, params.getString("md5securityKey"), TenpayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[preparePayInfoMobile] 财付通订单支付签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        //3.组装访问url, 并且获取预支付ID
        TenpayHttpClient httpClient = new TenpayHttpClient();

        Result httpResponse = httpClient.doGet(params.getString("prepayUrl"), requestPMap);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("[preparePayInfoMobile] 财付通订单支付HTTP请求失败, 参数:"
                    + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_HTTP_ERROR);
        }
        String resContent = (String) httpResponse.getReturnValue();
        PMap tenpayMap;
        try {
            tenpayMap = XMLUtil.XML2PMap(resContent);
        } catch (Exception e) {
            log.error("[preparePayInfoMobile] 财付通订单支付解析响应报文异常, 参数:"
                    + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_XML_PARSE_ERROR);
        }
        if (tenpayMap == null) {
            log.error("[preparePayInfoMobile] 财付通订单支付解析响应报文异常, 参数:"
                    + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_XML_PARSE_ERROR);
        }

        String token_id = (String) tenpayMap.get("token_id");
        if (token_id == null) {
            log.error("[preparePayInfoMobile] 财付通订单支付返回参数异常, token_id为空, 参数:"
                    + tenpayMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_RESPONSE_PARAM_ERROR);
        }
        result.addItem("token_id", token_id);
        return result;
    }

    @Override
    public ResultMap preparePayInfoSDK(PMap params) throws ServiceException {
        return preparePayInfoMobile(params, null);
    }


    //https://www.tenpay.com/app/mpay/wappay_init.cgi
    //https://www.tenpay.com/app/mpay/mp_gate.cgi
    @Override
    public ResultMap preparePayInfoWap(PMap params) throws ServiceException {
        String callback_url = params.getString("pageNotifyUrl");
        ResultMap result = preparePayInfoMobile(params, callback_url);
        String token_id = (String) result.getItem("token_id");
        PMap requestPMap = new PMap();
        requestPMap.put("token_id", token_id);
        String returnUrl = HttpUtil.packHttpGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;
    }

    /**
     * 4.财付通查询订单信息
     * <p/>
     * 只能查询半年内的订单, 超过半年的订单调用此查询接口会报“88221009交易单不存在”
     */
    @Override
    public ResultMap queryOrder(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装查询参数
        PMap requestPMap = new PMap();
        requestPMap.put("input_charset", TenpayService.INPUT_CHARSET);
        requestPMap.put("partner", params.getString("merchantNo"));
        requestPMap.put("out_trade_no", params.getString("serialNumber"));
        requestPMap.put("sign_type", TenpayService.SIGN_TYPE);
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[queryOrder] 财付通订单查询参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_PARAM_ERROR);
        }
        //2.获取md5签名
        String sign = SecretKeyUtil
                .tenMD5Sign(requestPMap, params.getString("md5securityKey"), TenpayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[queryOrder] 财付通订单查询签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        //3.组装访问url
        TenpayHttpClient httpClient = new TenpayHttpClient();
        PMap tenpayMap;

        Result httpResponse = httpClient.doGet(params.getString("queryUrl"), requestPMap);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("[queryOrder] 财付通订单查询HTTP请求失败, 参数:"
                    + params.getString("queryUrl") + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_HTTP_ERROR);
        }
        String resContent = (String) httpResponse.getReturnValue();
        try {
            tenpayMap = XMLUtil.XML2PMap(resContent);
        } catch (Exception e) {
            log.error("[queryOrder] 财付通订单查询解析响应报文异常, 参数:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_XML_PARSE_ERROR);
        }
        if (tenpayMap == null) {
            log.error("[queryOrder] 财付通订单查询解析响应报文异常, 参数:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_XML_PARSE_ERROR);
        }

        //5.签名校验
        boolean
                signMd5 =
                SecretKeyUtil
                        .tenMD5CheckSign(tenpayMap, params.getString("md5securityKey"),
                                tenpayMap.getString("sign"), TenpayService.INPUT_CHARSET);
        if (!signMd5) {
            log.error("[queryOrder] 财付通订单查询返回参数签名错误, 参数:" + params + ", 返回:" + tenpayMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_SIGN_ERROR);
        }
        String retcode = tenpayMap.getString("retcode");
        if (TenpayUtils.isEmpty(retcode) || !retcode.equals("0")) {
            log.error("[queryOrder] 财付通订单查询返回参数异常, retcode!=0, 参数:" + params + ", 返回:" + tenpayMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        }
        String trade_state = getTradeStatus(tenpayMap.getString("trade_state"));
        result.addItem("order_state", trade_state);

        return result;
    }

    private String getTradeStatus(String tenpayTradeStatus) {
        if (tenpayTradeStatus == null) return TenpayService.TRADE_STATUS.get("DEFAULT");
        String trade_status = TenpayService.TRADE_STATUS.get(tenpayTradeStatus);
        if (trade_status == null) return TenpayService.TRADE_STATUS.get("DEFAULT");
        return trade_status;
    }

    private String getRefundStatus(String tenpayRefundStatus) {
        if (tenpayRefundStatus == null) return TenpayService.REFUND_STATUS.get("DEFAULT");
        String refund_status = TenpayService.REFUND_STATUS.get(tenpayRefundStatus);
        if (refund_status == null) return TenpayService.REFUND_STATUS.get("DEFAULT");
        return refund_status;
    }

    /**
     * 5.财付通订单退款
     * <p/>
     * 只能退半年内的订单, 超过半年的订单调用此退款接口会报“88221009交易单不存在”
     */
    @Override
    public ResultMap refundOrder(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap requestPMap = new PMap();
        //1.组装参数开始
        requestPMap.put("input_charset", TenpayService.INPUT_CHARSET);         //编码
        requestPMap.put("partner", params.getString("merchantNo"));         // 商户号
        requestPMap.put("service_version", "1.1");
        requestPMap.put("out_trade_no", params.getString("serialNumber"));  // 商户订单号
        requestPMap.put("out_refund_no", params.getString("refundSerialNumber"));            // 商户退款单号
        String totalAmount = TenpayUtils.fenParseFromYuan(params.getString("totalAmount"));
        requestPMap.put("total_fee", totalAmount);                          // 总金额,调用端添加
        String refundAmount = TenpayUtils.fenParseFromYuan(params.getString("refundAmount"));
        requestPMap.put("refund_fee", refundAmount);                        // 退款金额
        requestPMap.put("notify_url", params.getString("refundNotifyUrl"));         //异步回调地址
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[refundOrder] 财付通退款参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_REFUND_PARAM_ERROR);
        }
        String[] op_user_pwds = TenpayService.REFUND_OPUSER.get(params.getString("merchantNo"));
        if (op_user_pwds == null) {
            log.error("[refundOrder] 财付通退款无法获取操作员, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_REFUND_SIGN_ERROR);
        }
        String op_user_id = op_user_pwds[0];//操作员账号
        String opUserPasswd = op_user_pwds[1];//操作员登录密码
        String certPasswd = op_user_pwds[2];//证书导入密码
        //操作员密码md5加密
        String op_user_passwd_md5 = MD5Util.MD5Encode(opUserPasswd, TenpayService.INPUT_CHARSET).toUpperCase();
        requestPMap.put("op_user_id", op_user_id);               // 商户账号
        requestPMap.put("op_user_passwd", op_user_passwd_md5); // 商户账号密码密钥md5加密之后

        //2.获得加密密钥, 进行参数加密
        String md5securityKey = params.getString("md5securityKey");         // 加密秘钥
        String sign =
                SecretKeyUtil.tenMD5Sign(requestPMap, md5securityKey, TenpayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[refundOrder] 财付通退款签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_REFUND_SIGN_ERROR);
        }
        requestPMap.put("sign", sign); // 密钥
        // 3.获取支付机构请求报文处理配置
        TenpayHttpClient httpClient = new TenpayHttpClient();
        PMap tenpayMap;
        httpClient.setCertFile(params.getString("privateCertFilePath"),
                certPasswd, params.getString("publicCertFilePath"));
        Result httpResponse = httpClient.doGet(params.getString("refundUrl"), requestPMap);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("[refundOrder] 财付通退款HTTP请求失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_REFUND_HTTP_ERROR);
        }
        String resContent = (String) httpResponse.getReturnValue();
        try {
            tenpayMap = XMLUtil.XML2PMap(resContent);
        } catch (Exception ex) {
            log.error("[refundOrder] 财付通退款解析响应报文异常, 参数:" + params + "返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_REFUND_XML_PARSE_ERROR);
        }
        if (tenpayMap == null) {
            log.error("[refundOrder] 财付通退款解析响应报文异常, 参数:" + params + "返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_REFUND_XML_PARSE_ERROR);
        }

        //6.签名校验
        boolean
                signMd5 =
                SecretKeyUtil
                        .tenMD5CheckSign(tenpayMap, md5securityKey, tenpayMap.getString("sign"),
                                TenpayService.INPUT_CHARSET);
        if (!signMd5) {
            log.error("[refundOrder] 财付通退款返回参数签名错误, 参数:" + requestPMap + "返回:"
                    + resContent);
            return ResultMap.build(ResultStatus.THIRD_REFUND_RESPONSE_SIGN_ERROR);
        }
        String retcode = tenpayMap.getString("retcode");
        if (TenpayUtils.isEmpty(retcode) || !"0".equals(retcode)) {
            log.error("[refundOrder] 财付通退款返回参数异常, retcode!=0, 参数:" + requestPMap + "返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_REFUND_RESPONSE_PARAM_ERROR);
        }
        String refund_status = tenpayMap.getString("refund_status");
        String error_code = refund_status;
        String error_msg = getRefundStatus(refund_status);
        if (error_msg.equals(OrderRefundStatus.FAIL)) {
            result.addItem("error_code", error_code);
            result.addItem("error_msg", error_msg);
            result.withError(ResultStatus.THIRD_REFUND_RESPONSE_PARAM_ERROR);
        }
        return result;
    }


    /**
     * 6.财付通查询订单退款信息
     * <p/>
     * 只能查询半年内的订单, 超过半年的订单调用此查询接口会报“88221009交易单不存在”
     */
    @Override
    public ResultMap queryRefundOrder(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装查询参数
        PMap requestPMap = new PMap();
        requestPMap.put("input_charset", TenpayService.INPUT_CHARSET);
        requestPMap.put("partner", params.getString("merchantNo"));
        requestPMap.put("out_refund_no", params.getString("out_refund_no"));
        requestPMap.put("service_version", "1.1");
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[queryRefundOrder] 财付通退款查询参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_PARAM_ERROR);
        }
        //2.获取md5签名
        String sign = SecretKeyUtil
                .tenMD5Sign(requestPMap, params.getString("md5securityKey"), TenpayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[queryRefundOrder] 财付通退款查询签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        //3.组装访问url
        TenpayHttpClient httpClient = new TenpayHttpClient();
        PMap tenpayMap;
        Result httpResponse = httpClient.doGet(params.getString("queryRefundUrl"), requestPMap);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("[queryRefundOrder] 财付通退款查询HTTP请求失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_HTTP_ERROR);
        }
        String resContent = (String) httpResponse.getReturnValue();
        try {
            tenpayMap = XMLUtil.XML2PMap(resContent);
        } catch (Exception ex) {
            log.error("[queryRefundOrder] 财付通退款查询解析响应报文异常, 参数:" + params + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_XML_PARSE_ERROR);
        }
        if (tenpayMap == null) {
            log.error("[queryRefundOrder] 财付通退款查询解析响应报文异常, 参数:" + params + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_XML_PARSE_ERROR);
        }

        //5.签名校验
        boolean
                signMd5 =
                SecretKeyUtil
                        .tenMD5CheckSign(tenpayMap, params.getString("md5securityKey"),
                                tenpayMap.getString("sign"), TenpayService.INPUT_CHARSET);
        if (!signMd5) {
            log.error("[queryRefundOrder] 财付通退款查询返回参数签名错误, 参数:" + params + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_RESPONSE_SIGN_ERROR);
        }
        String retcode = tenpayMap.getString("retcode");
        if (TenpayUtils.isEmpty(retcode) || !retcode.equals("0")) {
            log.error("[queryRefundOrder] 财付通退款查询返回参数异常, retcode!=0, 参数:" + params + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_RESPONSE_PARAM_ERROR);
        }
        String refund_state = getRefundStatus(tenpayMap.getString("refund_state_0"));
        result.addItem("refund_status", refund_state);
        return result;
    }


    @Override
    public ResultMap downloadOrder(PMap params) throws ServiceException {

        ResultMap result = ResultMap.build();
        // yyyyMMdd -> yyyy-MM-dd
        Date checkDate = (Date) params.get("checkDate");
        String tenpayCheckDate = DateUtil.format(checkDate, DateUtil.DATE_FORMAT_DAY);

        StringBuilder sb = new StringBuilder();
        // 参数需严格按照以下顺序添加，会影响签名
        sb.append("spid=").append(params.getString("merchantNo")).append("&");
        sb.append("trans_time=").append(tenpayCheckDate).append("&");
        sb.append("stamp=").append(String.valueOf(System.currentTimeMillis())).append("&");
        /**
         * 0:返回当日成功的订单
         * 1：返回当日成功支付的订单
         * 2：返回当日退款的订单
         */
        CheckType checkType = (CheckType) params.get("checkType");
        if (checkType == CheckType.ALL) {
            sb.append("mchtype=0");
        } /*else if (checkType == CheckType.PAID) {
            sb.append("mchtype=1");
        } else if (checkType == CheckType.REFUND) {
            sb.append("mchtype=2");
        }*/ else {
            log.error("[downloadOrder] 财付通下载对账单参数错误, 参数:" + params);
            result.withError(ResultStatus.THIRD_QUERY_PARAM_ERROR);
            return result;
        }
        //2.获取md5签名
        String sign = SecretKeyUtil.tenMD5Sign(sb.toString(), params.getString("key"), TenpayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[downloadOrder] 财付通下载对账单签名失败, 参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_QUERY_SIGN_ERROR);
        }
        String paramString = sb.append("&sign=").append(sign).toString();

        String[] op_user_pwds = TenpayService.REFUND_OPUSER.get(params.getString("merchantNo"));
        if (op_user_pwds == null) {
            log.error("[downloadOrder] 财付通下载对账单无法获取操作员, 参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_QUERY_PARAM_ERROR);
        }
        String certPasswd = op_user_pwds[2];

        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setCharset("GBK");
        httpClient.setCertFile(params.getString("privateCertFilePath"),
                certPasswd, params.getString("publicCertFilePath"));
        Result httpResponse = httpClient.doGet(params.getString("downloadUrl"), paramString);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("[downloadOrder] 财付通下载对账单HTTP请求失败, 参数:" + paramString);
            result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
            return result;
        }
        String resContent = (String) httpResponse.getReturnValue();
        return validateAndParseMessage(resContent);
    }

    private ResultMap validateAndParseMessage(String message) {

        ResultMap result = ResultMap.build();
        String line = null;
        BufferedReader reader = null;
        List<OutCheckRecord> payRecords = new LinkedList<OutCheckRecord>();
        List<OutCheckRecord> refRecords = new LinkedList<OutCheckRecord>();
        System.out.println(message);
        try {
            result.addItem("hasNextPage", false);

            if (message.startsWith("<html>")) {
                log.error("[validateAndParseMessage] 财付通解析对账单返回参数异常, 返回:" + message);
                PMap pMap = XMLUtil.XML2PMap(message);
                String errorText = pMap.getString("body").trim();
                //03020003:该日期对帐单还没有生成
                //03020123:没有生成对账单文件，或没有符合条件的交易记录
                //03020120:昨日对账单未生成或没有符合条件交易记录，请稍候再试或进入交易管理查询
                if (errorText.startsWith("03020")) {
                    return result;
                }
                result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
                return result;
            }
            SimpleDateFormat df = new SimpleDateFormat("`yyyy-MM-dd HH:mm:ss");
            reader = new BufferedReader(new StringReader(message));
            line = reader.readLine();// 第一行是表头，忽略
            if (line == null) {
                log.error("[validateAndParseMessage] 财付通解析对账单返回参数异常, 返回:" + message);
                result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
                return result;
            }

            while ((line = reader.readLine()) != null) {
                //汇总标题 结束
                if (!line.startsWith("`")) {
                    break;
                }

                String[] parts = line.split(",");
                OutCheckRecord record = new OutCheckRecord();
                String flag = parts[5].trim();
                //判断支付 or 退款
                if ("用户已支付".equals(flag)) {
                    //交易完成时间
                    record.setOutTransTime(df.parse(parts[0].trim()));
                    //第三方流水号
                    record.setOutPayNo(parts[1].trim().replaceFirst("`", ""));
                    //我方流水号
                    record.setPayNo(parts[2].trim().replaceFirst("`", ""));
                    //交易金额
                    BigDecimal money = BigDecimal.valueOf(Double.parseDouble(parts[6].trim()));
                    record.setMoney(money);
                    //手续费
                    record.setCommssionFee(BigDecimal.ZERO);
                    payRecords.add(record);
                } else if ("转入退款".equals(flag)) {
                    //交易完成时间
                    record.setOutTransTime(df.parse(parts[0].trim()));
                    //第三方流水号
                    record.setOutPayNo(parts[1].trim().replaceFirst("`", ""));
                    //我放流水号
                    record.setPayNo(parts[7].trim().replaceFirst("`", ""));
                    //交易金额
                    BigDecimal money = BigDecimal.valueOf(Double.parseDouble(parts[8].trim()));
                    record.setMoney(money);
                    //手续费
                    record.setCommssionFee(BigDecimal.ZERO);
                    refRecords.add(record);
                }
            }
            result.addItem("payRecords", payRecords);
            result.addItem("refRecords", refRecords);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[validateAndParseMessage] 财付通解析对账单返回参数异常, 返回:" + line);
            result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        }
        return result;
    }

    @Override
    public ResultMap prepareTransferInfo(PMap params) throws ServiceException {
        throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
    }

    public ResultMap getReqIDFromNotifyWebSync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        String out_trade_no = params.getString("out_trade_no");
        if (out_trade_no == null) {
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
            return result;
        }
//        String partner = params.getString("partner");
        result.addItem("reqId", out_trade_no);//商户网站唯一订单号
//        result.addItem("merchantNo", partner);//商户号
        return result;
    }

    public ResultMap getReqIDFromNotifyWebAsync(PMap params) throws ServiceException {
        return getReqIDFromNotifyWebSync(params);
    }

    public ResultMap getReqIDFromNotifyWapSync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        String sp_billno = params.getString("sp_billno");
        if (sp_billno == null) {
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
            return result;
        }
//        String bargainor_id = params.getString("bargainor_id");
        result.addItem("reqId", sp_billno);//商户网站唯一订单号
//        result.addItem("merchantNo", bargainor_id);//商户号
        return result;
    }

    public ResultMap getReqIDFromNotifyWapAsync(PMap params) throws ServiceException {
        return getReqIDFromNotifyWapSync(params);
    }

    public ResultMap getReqIDFromNotifySDKAsync(PMap params) throws ServiceException {
        throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
    }

    public ResultMap getReqIDFromNotifyRefund(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        String out_refund_no = params.getString("out_refund_no");
        if (out_refund_no == null) {
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
            return result;
        }
//        String partner = params.getString("partner");
        result.addItem("reqId", out_refund_no);//商户网站唯一订单号
//        result.addItem("merchantNo", partner);//商户号
        return result;
    }

    public ResultMap getReqIDFromNotifyTransfer(PMap params) throws ServiceException {
        throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
    }

    public ResultMap handleNotifyWebSync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap notifyParams = params.getPMap("data");
        //校验签名
        String md5securityKey = params.getString("md5securityKey");
        String out_sign = notifyParams.getString("sign");
        if (!SecretKeyUtil.tenMD5CheckSign(notifyParams, md5securityKey, out_sign, TenpayService.INPUT_CHARSET)) {
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
            return result;
        }
        //提取关键参数
        String out_trade_no = notifyParams.getString("out_trade_no");
        String trade_state = getTradeStatus(notifyParams.getString("trade_state"));

        result.addItem("reqId", out_trade_no);//商户网站唯一订单号
        result.addItem("tradeStatus", trade_state);//交易状态

        return result;
    }

    public ResultMap handleNotifyWebAsync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap notifyParams = params.getPMap("data");
        //校验签名
        String md5securityKey = params.getString("md5securityKey");
        String out_sign = notifyParams.getString("sign");
        if (!SecretKeyUtil.tenMD5CheckSign(notifyParams, md5securityKey, out_sign, TenpayService.INPUT_CHARSET)) {
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
            return result;
        }
        //提取关键参数
        String out_trade_no = notifyParams.getString("out_trade_no");
        String transaction_id = notifyParams.getString("transaction_id");
        String trade_state = getTradeStatus(notifyParams.getString("trade_state"));
        String time_end = notifyParams.getString("time_end");
        String total_fee = notifyParams.getString("total_fee");
        total_fee = String.valueOf(new BigDecimal(total_fee).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UP));

        result.addItem("reqId", out_trade_no);//商户网站唯一订单号
        result.addItem("agencyOrderId", transaction_id);//第三方订单号
        result.addItem("tradeStatus", trade_state);//交易状态
        result.addItem("agencyPayTime", time_end);//第三方支付时间
        result.addItem("trueMoney", total_fee);//支付金额

        return result;
    }

    public ResultMap handleNotifyWapSync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap notifyParams = params.getPMap("data");
        //校验签名
        String md5securityKey = params.getString("md5securityKey");
        String out_sign = notifyParams.getString("sign");
        if (!SecretKeyUtil.tenMD5CheckSign(notifyParams, md5securityKey, out_sign, TenpayService.INPUT_CHARSET)) {
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
            return result;
        }
        //提取关键参数
        String sp_billno = notifyParams.getString("sp_billno");
        String pay_result = getTradeStatus(notifyParams.getString("pay_result"));

        result.addItem("reqId", sp_billno);//商户网站唯一订单号
        result.addItem("tradeStatus", pay_result);//交易状态

        return result;
    }

    public ResultMap handleNotifyWapAsync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap notifyParams = params.getPMap("data");
        //校验签名
        String md5securityKey = params.getString("md5securityKey");
        String out_sign = notifyParams.getString("sign");
        if (!SecretKeyUtil.tenMD5CheckSign(notifyParams, md5securityKey, out_sign, TenpayService.INPUT_CHARSET)) {
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
            return result;
        }
        //提取关键参数
        String sp_billno = notifyParams.getString("sp_billno");
        String transaction_id = notifyParams.getString("transaction_id");
        String pay_result = getTradeStatus(notifyParams.getString("pay_result"));
        String time_end = notifyParams.getString("time_end");
        String total_fee = notifyParams.getString("total_fee");
        total_fee = String.valueOf(new BigDecimal(total_fee).divide(new BigDecimal(100), 2, BigDecimal.ROUND_UP));

        result.addItem("reqId", sp_billno);//商户网站唯一订单号
        result.addItem("agencyOrderId", transaction_id);//第三方订单号
        result.addItem("tradeStatus", pay_result);//交易状态
        result.addItem("agencyPayTime", time_end);//第三方支付时间
        result.addItem("trueMoney", total_fee);//支付金额

        return result;
    }

    public ResultMap handleNotifySDKAsync(PMap params) throws ServiceException {
        throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
    }

    public ResultMap handleNotifyRefund(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap notifyParams = params.getPMap("data");
        //校验签名
        String md5securityKey = params.getString("md5securityKey");
        String out_sign = notifyParams.getString("sign");
        if (!SecretKeyUtil.tenMD5CheckSign(notifyParams, md5securityKey, out_sign, TenpayService.INPUT_CHARSET)) {
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
            return result;
        }
        //提取关键参数
        String out_refund_no = notifyParams.getString("out_refund_no");
        String refund_id = notifyParams.getString("refund_id");
        String refund_fee = notifyParams.getString("refund_fee");
        refund_fee = String.valueOf(new BigDecimal(refund_fee).divide(new BigDecimal(100), 2, BigDecimal.ROUND_UP));
        String refund_status = getRefundStatus(notifyParams.getString("refund_status"));

        result.addItem("reqId", out_refund_no);//商户网站唯一退款单号
        result.addItem("agencyRefundId", refund_id);//第三方退款单号
        result.addItem("refundStatus", refund_status);//交易状态
        result.addItem("refundMoney", refund_fee);//支付金额

        return result;
    }

    public ResultMap handleNotifyTransfer(PMap params) throws ServiceException {
        throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
    }

}
