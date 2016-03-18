package com.sogou.pay.thirdpay.service.Alipay;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.common.utils.XMLUtil;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.thirdpay.biz.model.OutCheckRecord;
import com.sogou.pay.thirdpay.biz.model.TransferRecord;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.common.http.client.*;

import com.sogou.pay.thirdpay.biz.enums.AlipayTradeCode;
import com.sogou.pay.thirdpay.service.ThirdpayService;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by xiepeidong on 2016/1/14.
 */
@Service
public class AlipayService implements ThirdpayService {
    private static final Logger log = LoggerFactory.getLogger(AlipayService.class);
    public static final String ALIPAY_SERVICE_DIRECTPAY = "create_direct_pay_by_user";
    public static final String ALIPAY_SERVICE_WAP_DIRECTPAY = "alipay.wap.create.direct.pay.by.user";
    public static final String ALIPAY_SERVICE_MOBILE_DIRECTPAY = "mobile.securitypay.pay";
    public static final String ALIPAY_SERVICE_QUERY = "single_trade_query"; //支付宝查询订单接口名
    public static final String ALIPAY_SERVICE_REFUND = "refund_fastpay_by_platform_nopwd"; //支付宝订单退款接口名
    public static final String ALIPAY_SERVICE_QUERY_REFUND = "refund_fastpay_query"; //支付宝查询退款接口名
    public final static String ALIPAY_SERVICE_PAGE_QUERY = "account.page.query";//财务明细分页查询接口
    public final static String ALIPAY_SERVICE_BATCH_TRANS = "batch_trans_notify";//批量付款到支付宝账户


    public static final String INPUT_CHARSET = "utf-8";                           // 字符编码格式 utf-8
    public static final String PAYMENT_TYPE = "1";                                //支付类型
    public static final String SIGN_TYPE = "MD5";                                 //签名方式
    /**
     * 支付宝扫码支付参数
     */
    // 1） 简约前置模式：qr_pay_mode=0;
    // 2） 前置模式：qr_pay_mode=1;
    // 3） 页面跳转模式：这个参数的值 qr_pay_mode=2 , 直接进入到支付宝收银台
    public static String QR_PAY_MODE = "0";   //扫码支付模式
    /**
     * 支付宝钱包支付, 调用支付宝接口名
     */
    // 支付宝钱包支付, 超时时间设置 , 默认30分钟, 一旦超时, 该笔交易就会自动被关闭, 取值范围：1m～15d。
    // m-分钟, h-小时, d-天, 1c-当天（无论交易何时创建, 都在0点关闭）。
    // 该参数数值不接受小数点, 如1.5h, 可转换为90m。
    public static final String IT_B_PAY = "30m";

    private static HashMap<String, String> TRADE_STATUS = new HashMap<String, String>();
    private static HashMap<CheckType, String> CHECK_TYPE = new HashMap<CheckType, String>();

    static {
        TRADE_STATUS.put("TRADE_PENDING", OrderStatus.SUCCESS.name());//等待卖家收款
        TRADE_STATUS.put("TRADE_FINISHED", OrderStatus.SUCCESS.name());//交易成功结束
        TRADE_STATUS.put("TRADE_SUCCESS", OrderStatus.SUCCESS.name());//支付成功
        TRADE_STATUS.put("BUYER_PRE_AUTH", OrderStatus.SUCCESS.name());//买家已付款（语音支付）
        TRADE_STATUS.put("WAIT_SELLER_SEND_GOODS", OrderStatus.SUCCESS.name());//买家已付款, 等待卖家发货
        TRADE_STATUS.put("WAIT_BUYER_CONFIRM_GOODS", OrderStatus.SUCCESS.name());//卖家已发货, 等待买家确认
        TRADE_STATUS.put("WAIT_SYS_PAY_SELLER", OrderStatus.SUCCESS.name());//买家确认收货, 等待支付宝打款给卖家
        TRADE_STATUS.put("COD_WAIT_SYS_PAY_SELLER", OrderStatus.SUCCESS.name());//签收成功等待系统打款给卖家（货到付款）
        TRADE_STATUS.put("WAIT_BUYER_PAY", OrderStatus.NOTPAY.name());//等待买家付款
        TRADE_STATUS.put("COD_WAIT_SELLER_SEND_GOODS", OrderStatus.NOTPAY.name());//等待卖家发货（货到付款）
        TRADE_STATUS.put("COD_WAIT_BUYER_PAY", OrderStatus.NOTPAY.name());//等待买家签收付款（货到付款）
        TRADE_STATUS.put("TRADE_CLOSED", OrderStatus.CLOSED.name());//交易中途关闭（已结束, 未成功完成）
        TRADE_STATUS.put("TRADE_CANCEL", OrderStatus.CLOSED.name());//立即支付交易取消
        TRADE_STATUS.put("WAIT_SYS_CONFIRM_PAY", OrderStatus.USERPAYING.name());//支付宝确认买家银行汇款中, 暂勿发货
        TRADE_STATUS.put("TRADE_REFUSE", OrderStatus.FAILURE.name());//立即支付交易拒绝
        TRADE_STATUS.put("TRADE_REFUSE_DEALING", OrderStatus.FAILURE.name());//立即支付交易拒绝中
        TRADE_STATUS.put("DEFAULT", OrderStatus.FAILURE.name());//默认

        CHECK_TYPE.put(CheckType.ALL, StringUtil.joinStrings(",", AlipayTradeCode.TRADE_CODE_PAY.getValue(),
                AlipayTradeCode.TRADE_CODE_TRANSFER.getValue(),
                AlipayTradeCode.TRADE_CODE_CHARGE.getValue(),
                AlipayTradeCode.TRADE_CODE_CASH.getValue()));
        CHECK_TYPE.put(CheckType.PAID, AlipayTradeCode.TRADE_CODE_PAY.getValue());
        CHECK_TYPE.put(CheckType.REFUND, AlipayTradeCode.TRADE_CODE_TRANSFER.getValue());
        CHECK_TYPE.put(CheckType.CHARGED, AlipayTradeCode.TRADE_CODE_CHARGE.getValue());
        CHECK_TYPE.put(CheckType.WITHDRAW, AlipayTradeCode.TRADE_CODE_CASH.getValue());
    }

    @Override
    public ResultMap preparePayInfoAccount(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.根据文档说明, 组装参数
        PMap requestPMap = new PMap();
        requestPMap.put("service", AlipayService.ALIPAY_SERVICE_DIRECTPAY);             //接口名称
        requestPMap.put("partner", params.getString("merchantNo"));             //合作者身份ID
        requestPMap.put("_input_charset", AlipayService.INPUT_CHARSET);            //参数编码
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));     //服务器异步通知页面路径
        requestPMap.put("return_url", params.getString("pageNotifyUrl"));       //页面跳转同步通知页面路径（可空）
        requestPMap.put("out_trade_no", params.getString("serialNumber"));      //商户网站唯一订单号
        requestPMap.put("subject", params.getString("subject"));                //商品名称
        requestPMap.put("payment_type", AlipayService.PAYMENT_TYPE);               //支付类型
        requestPMap.put("seller_id", params.getString("merchantNo"));             //卖家支付宝账户号
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        requestPMap.put("total_fee", orderAmount);                                    //交易金额
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[preparePayInfoAccount] 支付宝订单支付参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_PARAM_ERROR);
        }
        String md5securityKey = params.getString("md5securityKey");
        //2.获取md5签名
        String
                sign =
                SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, AlipayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[preparePayInfoAccount] 支付宝订单支付签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        requestPMap.put("sign_type", AlipayService.SIGN_TYPE);                     //签名方式

        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;
    }

    @Override
    public ResultMap preparePayInfoGatway(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装参数
        PMap requestPMap = new PMap();
        requestPMap.put("service", AlipayService.ALIPAY_SERVICE_DIRECTPAY);                   //接口名称
        requestPMap.put("partner", params.getString("merchantNo"));                   //合作者身份ID
        requestPMap.put("_input_charset", AlipayService.INPUT_CHARSET);                  //参数编码
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));           //服务器异步通知页面路径
        requestPMap.put("return_url", params.getString("pageNotifyUrl"));             //页面跳转同步通知页面路径（可空）
        requestPMap.put("out_trade_no", params.getString("serialNumber"));            //商户网站唯一订单号
        /**账户支付和网关支付区别参数开始**/
        requestPMap.put("paymethod", "bankPay");//默认支付方式
        requestPMap.put("defaultbank", params.get("bankCode"));//默认银行
        /**账户支付和网关支付区别参数结束**/
        requestPMap.put("subject", params.getString("subject"));                      //商品名称
        requestPMap.put("payment_type", AlipayService.PAYMENT_TYPE);                     //支付类型
        requestPMap.put("seller_id", params.getString("merchantNo"));             //卖家支付宝账户号
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        requestPMap.put("total_fee", orderAmount);
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[preparePayInfoGatway] 支付宝订单支付参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_PARAM_ERROR);
        }
        String md5securityKey = params.getString("md5securityKey");
        //2.获取md5签名
        String
                sign = SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, AlipayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[preparePayInfoGatway] 支付宝订单支付签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        requestPMap.put("sign_type", AlipayService.SIGN_TYPE);                           //签名方式
        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;
    }

    @Override
    public ResultMap preparePayInfoQRCode(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.根据文档说明, 组装参数
        PMap requestPMap = new PMap();
        requestPMap.put("service", AlipayService.ALIPAY_SERVICE_DIRECTPAY);             //接口名称
        requestPMap.put("partner", params.getString("merchantNo"));             //合作者身份ID
        requestPMap.put("_input_charset", AlipayService.INPUT_CHARSET);            //参数编码
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));     //服务器异步通知页面路径
        requestPMap.put("return_url", params.getString("pageNotifyUrl"));       //页面跳转同步通知页面路径（可空）
        requestPMap.put("out_trade_no", params.getString("serialNumber"));      //商户网站唯一订单号
        requestPMap.put("subject", params.getString("subject"));                //商品名称
        requestPMap.put("payment_type", AlipayService.PAYMENT_TYPE);               //支付类型
        requestPMap.put("seller_id", params.getString("merchantNo"));             //卖家支付宝账户号
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        requestPMap.put("total_fee", orderAmount);                                    //支付金额
        String md5securityKey = params.getString("md5securityKey");
        /**账户支付和扫码支付区别参数开始**/
        requestPMap.put("qr_pay_mode", AlipayService.QR_PAY_MODE);
        /**账户支付和扫码关支付区别参数结束**/
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[preparePayInfoQRCode] 支付宝订单支付参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_PARAM_ERROR);
        }
        //2.获取md5签名
        String
                sign =
                SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, AlipayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[preparePayInfoQRCode] 支付宝订单支付签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        requestPMap.put("sign_type", AlipayService.SIGN_TYPE);                     //签名方式
        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;
    }

    @Override
    public ResultMap preparePayInfoSDK(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        // 1.组装签名用到的参数
        PMap requestPMap = new PMap();
        requestPMap.put("service", AlipayService.ALIPAY_SERVICE_MOBILE_DIRECTPAY);             //接口名称
        requestPMap.put("partner", params.getString("merchantNo"));             //合作者身份ID
        requestPMap.put("_input_charset", AlipayService.INPUT_CHARSET);            //参数编码
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));     //服务器异步通知页面路径
        requestPMap.put("out_trade_no", params.getString("serialNumber"));      //商户网站唯一订单号
        requestPMap.put("subject", params.getString("subject"));                //商品名称
        requestPMap.put("body", params.getString("subject"));                //商品名称
        requestPMap.put("payment_type", AlipayService.PAYMENT_TYPE);               //支付类型
        requestPMap.put("seller_id", params.getString("merchantNo"));             //卖家支付宝账户号
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        requestPMap.put("total_fee", orderAmount);                                    //支付金额
        // 11.设置未付款交易的超时时间
        // 默认30分钟, 一旦超时, 该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟, h-小时, d-天, 1c-当天（无论交易何时创建, 都在0点关闭）。
        // 该参数数值不接受小数点, 如1.5h, 可转换为90m。
        requestPMap.put("it_b_pay", AlipayService.IT_B_PAY);
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[preparePayInfoSDK] 支付宝订单支付参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_PARAM_ERROR);
        }
        // 2.获取商户私钥路径
        String privateCertFilePath = params.getString("privateCertFilePath");
        // 3.获取商户私钥
        String privateCertKey = SecretKeyUtil.loadKeyFromFile(privateCertFilePath);
        if (privateCertKey.equals("")) {
            log.error("[preparePayInfoSDK] 支付宝订单支付获取第三方支付账户密钥失败, 参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_PAY_GET_KEY_ERROR);
        }
        // 4.签名
        String
                sign =
                SecretKeyUtil.aliRSASign(requestPMap, privateCertKey, AlipayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[preparePayInfoSDK] 支付宝订单支付签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_SIGN_ERROR);
        }
        // 6.组装商户需要的订单信息参数
        requestPMap.put("sign", sign);
        requestPMap.put("sign_type", "RSA");                     //签名方式
        String payInfo = HttpUtil.packUrlParams(requestPMap, "\"");
        // 7.获取客户端需要的支付宝公钥
        String publicCertFilePath = params.getString("publicCertFilePath");
        String publicCertKey = SecretKeyUtil.loadKeyFromFile(publicCertFilePath);
        if (publicCertKey.equals("")) {
            log.error("[preparePayInfoSDK] 支付宝订单支付获取第三方支付账户公钥失败, 参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_PAY_GET_KEY_ERROR);
        }
        result.addItem("strOrderInfo", payInfo);
        result.addItem("aliPublicKey", publicCertKey);
        return result;
    }

    @Override
    public ResultMap preparePayInfoWap(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.根据文档说明, 组装参数
        PMap requestPMap = new PMap();
        requestPMap.put("service", AlipayService.ALIPAY_SERVICE_WAP_DIRECTPAY);             //接口名称
        requestPMap.put("partner", params.getString("merchantNo"));             //合作者身份ID
        requestPMap.put("_input_charset", AlipayService.INPUT_CHARSET);            //参数编码
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));     //服务器异步通知页面路径
        requestPMap.put("return_url", params.getString("pageNotifyUrl"));       //页面跳转同步通知页面路径（可空）
        requestPMap.put("out_trade_no", params.getString("serialNumber"));      //商户网站唯一订单号
        requestPMap.put("subject", params.getString("subject"));                //商品名称
        requestPMap.put("payment_type", AlipayService.PAYMENT_TYPE);               //支付类型
        requestPMap.put("seller_id", params.getString("merchantNo"));             //卖家支付宝账户号
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        requestPMap.put("total_fee", orderAmount);                                    //交易金额
        requestPMap.put("it_b_pay", AlipayService.IT_B_PAY);
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[preparePayInfoWap] 支付宝订单支付参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_PARAM_ERROR);
        }
        String md5securityKey = params.getString("md5securityKey");
        //2.获取md5签名
        String
                sign =
                SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, AlipayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[preparePayInfoWap] 支付宝订单支付签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        requestPMap.put("sign_type", AlipayService.SIGN_TYPE);                     //签名方式
        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;
    }

    @Override
    public ResultMap queryOrder(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        // 1.组装签名用到的参数
        PMap requestPMap = new PMap();
        requestPMap.put("service", AlipayService.ALIPAY_SERVICE_QUERY);                  //查询订单接口名
        requestPMap.put("partner", params.getString("merchantNo"));                //商户号
        requestPMap.put("_input_charset", AlipayService.INPUT_CHARSET);               //编码
        requestPMap.put("out_trade_no", params.getString("serialNumber"));             //订单号
        String md5securityKey = params.getString("md5securityKey");                //加密秘钥
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[queryOrder] 支付宝订单查询参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_PARAM_ERROR);
        }
        //2.获取md5签名
        String
                sign =
                SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, AlipayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[queryOrder] 支付宝订单查询签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        requestPMap.put("sign_type", AlipayService.SIGN_TYPE);                        //签名类型
        // 3.向支付机构发送查询请求
        Result httpResponse = HttpService.getInstance().doPost(params.getString("queryUrl"), requestPMap, AlipayService.INPUT_CHARSET, null);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("[queryOrder] 支付宝订单查询HTTP请求失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_HTTP_ERROR);
        }
        String resContent = (String) httpResponse.getReturnValue();
        // 4.解析响应
        PMap alipayMap;
        PMap responseMap;
        try {
            alipayMap = XMLUtil.XML2PMap(resContent);
            // 5.验证返回参数合法性
            if (alipayMap == null) {
                log.error("[queryOrder] 支付宝订单查询解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
                return ResultMap.build(ResultStatus.THIRD_QUERY_XML_PARSE_ERROR);
            }
            String alipayIsSuccess = alipayMap.getString("is_success");
            if (alipayIsSuccess.isEmpty()) {
                log.error("[queryOrder] 支付宝订单查询返回参数异常, 没有键值is_success, 参数:" + requestPMap + ", 返回:" + resContent);
                return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
            }
            if (!"T".equals(alipayIsSuccess.toUpperCase())) {
                log.error("[queryOrder] 支付宝订单查询返回参数异常, 状态is_success!=T, 参数:" + requestPMap + ", 返回:" + resContent);
                return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
            }
            responseMap = XMLUtil.XML2PMap(alipayMap.getString("response"));
        } catch (Exception e) {
            log.error("[queryOrder] 支付宝订单查询解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_XML_PARSE_ERROR);
        }
        // 6.获取对应退款单共同参数, 并返回
        String trade_status = getTradeStatus(responseMap.getString("trade_status"));
        result.addItem("order_state", trade_status);
        return result;
    }

    private String getTradeStatus(String alipayTradeStatus) {
        if (alipayTradeStatus == null) return AlipayService.TRADE_STATUS.get("DEFAULT");
        String trade_status = AlipayService.TRADE_STATUS.get(alipayTradeStatus);
        if (trade_status == null) return AlipayService.TRADE_STATUS.get("DEFAULT");
        return trade_status;
    }

    @Override
    public ResultMap refundOrder(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap requestPMap = new PMap();
        requestPMap.put("service", AlipayService.ALIPAY_SERVICE_REFUND);                     //接口名
        requestPMap.put("partner", params.getString("merchantNo"));                    //商户号
        requestPMap.put("_input_charset", AlipayService.INPUT_CHARSET);                   //编码
        requestPMap.put("batch_no", params.getString("refundSerialNumber")); //退款批次号
        requestPMap.put("refund_date", DateUtil.formatTime(params.getDate("refundReqTime")));                                     //退款请求时间
        requestPMap.put("batch_num", "1");                            //退款笔数
        BigDecimal oAmount = new BigDecimal(params.getString("refundAmount"));         //退款金额
        String refundAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        String refundInfo = params.getString("agencySerialNumber") + "^" + refundAmount + "^"
                + params.getString("refundSerialNumber");
        requestPMap.put("detail_data", refundInfo);
        requestPMap.put("notify_url", params.getString("refundNotifyUrl"));
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[refundOrder] 支付宝退款参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_REFUND_PARAM_ERROR);
        }
        String md5securityKey = params.getString("md5securityKey");
        String
                sign =
                SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, AlipayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[refundOrder] 支付宝退款签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_REFUND_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        requestPMap.put("sign_type", AlipayService.SIGN_TYPE);                            //加密类型
        // 获取支付机构请求报文处理配置
        Result httpResponse = HttpService.getInstance().doPost(params.getString("refundUrl"), requestPMap, AlipayService.INPUT_CHARSET, null);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("[refundOrder] 支付宝退款HTTP请求失败, 参数:"
                    + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_REFUND_HTTP_ERROR);
        }
        String resContent = (String) httpResponse.getReturnValue();
        PMap alipayMap;
        try {
            alipayMap = XMLUtil.XML2PMap(resContent);
        } catch (Exception e) {
            log.error("[refundOrder] 支付宝订单退款解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
            throw new ServiceException(e, ResultStatus.THIRD_REFUND_XML_PARSE_ERROR);
        }
        if (alipayMap == null) {
            log.error("[refundOrder] 支付宝订单退款解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_REFUND_XML_PARSE_ERROR);
        }
        String is_success = alipayMap.getString("is_success");
        if (StringUtil.isEmpty(is_success) || "F".equals(is_success)) {
            result.addItem("error_code", is_success);
            result.addItem("error_msg", alipayMap.getString("error"));
            result.withError(ResultStatus.THIRD_REFUND_RESPONSE_PARAM_ERROR);
        }
        return result;
    }

    @Override
    public ResultMap queryRefundOrder(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        // 1.组装签名用到的参数
        PMap requestPMap = new PMap();
        requestPMap.put("service", AlipayService.ALIPAY_SERVICE_QUERY_REFUND);                  //查询订单接口名
        requestPMap.put("partner", params.getString("merchantNo"));                //商户号
        requestPMap.put("_input_charset", AlipayService.INPUT_CHARSET);               //编码
        requestPMap.put("batch_no", params.getString("out_refund_no"));             //退款号
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[queryRefundOrder] 支付宝退款查询参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_PARAM_ERROR);
        }
        // 2.获取商户私钥
        String md5securityKey = params.getString("md5securityKey");                //加密秘钥
        // 3.签名
        String
                sign =
                SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, AlipayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[queryRefundOrder] 支付宝退款查询签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        requestPMap.put("sign_type", AlipayService.SIGN_TYPE);                        //签名类型

        // 4.获取支付机构请求报文处理配置
        Result httpResponse = HttpService.getInstance().doPost(params.getString("queryRefundUrl"), requestPMap, AlipayService.INPUT_CHARSET, null);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("[queryRefundOrder] 支付宝退款查询HTTP请求失败, 参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_HTTP_ERROR);
        }
        String resContent = (String) httpResponse.getReturnValue();
        //5.判断业务参数合法性
        if (resContent == null) {
            log.error("[queryRefundOrder] 支付宝退款查询返回参数异常, 返回为空, 参数:" + params + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_RESPONSE_PARAM_ERROR);
        }
        ResultMap refundResult = HttpUtil.extractUrlParams(resContent);
        String is_success = (String) refundResult.getItem("is_success");
        if (StringUtil.isEmpty(is_success)) {
            log.error("[queryRefundOrder] 支付宝退款查询返回参数异常, 没有键值is_success, 参数:" + params + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_RESPONSE_PARAM_ERROR);
        }
        if (!"T".equals(is_success.toUpperCase())) {
            log.error("[queryRefundOrder] 支付宝退款查询返回参数异常, 状态is_success!=T, 参数:" + params + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_RESPONSE_PARAM_ERROR);
        }
        String result_details = (String) refundResult.getItem("result_details");
        if (StringUtil.isEmpty(result_details)) {
            log.error(
                    "[queryRefundOrder] 支付宝退款查询返回参数异常, 没有键值result_details, 参数:" + params + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_RESPONSE_PARAM_ERROR);
        }
        String[] refundStrArr = result_details.split("\\^");
        String retunnRefundId = refundStrArr[0];
        String retunnRefundMon = refundStrArr[2];
        String refundIsSuccess = refundStrArr[3];
        if (StringUtil.isEmpty(retunnRefundId, retunnRefundMon, refundIsSuccess)) {
            log.error(
                    "[queryRefundOrder] 支付宝退款查询返回参数异常, result_details格式异常, 参数:" + params + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_RESPONSE_PARAM_ERROR);
        }
        if (!retunnRefundId.equals(params.getString("out_refund_no"))) {
            log.error(
                    "[queryRefundOrder] 支付宝退款查询返回参数异常, out_refund_no不匹配, 参数:" + params + ", 返回:" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_RESPONSE_PARAM_ERROR);
        }
        result.addItem("refund_status", refundIsSuccess);
        return result;
    }

    @Override
    public ResultMap downloadOrder(PMap params) throws ServiceException {


        ResultMap result = ResultMap.build();
        PMap requestPMap = new PMap();
        requestPMap.put("service", AlipayService.ALIPAY_SERVICE_PAGE_QUERY);//接口名称
        requestPMap.put("partner", params.getString("merchantNo"));//商户号
        requestPMap.put("_input_charset", AlipayService.INPUT_CHARSET);//编码字符集
        requestPMap.put("page_no", params.getString("pageNo"));//查询页号
        requestPMap.put("pageSize", params.getString("pageSize"));

        // yyyyMMdd -> yyyy-MM-dd
        Date checkDate = (Date) params.get("checkDate");
        String alipayCheckDate = DateUtil.format(checkDate, DateUtil.DATE_FORMAT_DAY);
        String startTime = alipayCheckDate + " 00:00:00";
        String endTime = alipayCheckDate + " 23:59:59";
        requestPMap.put("gmt_start_time", startTime);//账务查询开始时间
        requestPMap.put("gmt_end_time", endTime);//账务查询结束时间
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[downloadOrder] 支付宝下载对账单参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_QUERY_PARAM_ERROR);
        }

        CheckType checkType = (CheckType) params.get("checkType");
        if (checkType != CheckType.ALL) {
            log.error("[downloadOrder] 支付宝下载对账单参数错误, 参数:" + params);
            result.withError(ResultStatus.THIRD_QUERY_PARAM_ERROR);
            return result;
        }
        requestPMap.put("trans_code", AlipayService.CHECK_TYPE.get(checkType));

        //获取md5签名
        String sign = SecretKeyUtil.aliMD5Sign(requestPMap, params.getString("key"), AlipayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[downloadOrder] 支付宝下载对账单签名失败, 参数:" + requestPMap);
            result.withError(ResultStatus.THIRD_QUERY_SIGN_ERROR);
            return result;
        }
        requestPMap.put("sign", sign);
        requestPMap.put("sign_type", AlipayService.SIGN_TYPE); //签名类型
        // 获取支付机构请求报文处理配置
        // 4.获取支付机构请求报文处理配置
        Result httpResponse = HttpService.getInstance().doPost(params.getString("downloadUrl"), requestPMap, AlipayService.INPUT_CHARSET, null);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("[downloadOrder] 支付宝下载对账单HTTP请求失败, 参数:" + params);
            result.withError(ResultStatus.THIRD_QUERY_HTTP_ERROR);
            return result;
        }
        String resContent = (String) httpResponse.getReturnValue();
        //5.判断业务参数合法性
        if (resContent == null) {
            log.error("[downloadOrder] 支付宝下载对账单返回参数异常, 参数:" + params + ", 返回:" + resContent);
            result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
            return result;
        }

        return validateAndParseMessage(resContent);
    }


    private ResultMap validateAndParseMessage(String message) {
        ResultMap result = ResultMap.build();
        SAXReader reader = new SAXReader();
        reader.setEncoding(AlipayService.INPUT_CHARSET);
        Document doc = null;
        System.out.println(message);
        try {
            doc = reader.read(new StringReader(message));
        } catch (Exception e) {
            log.error("[validateAndParseMessage] 支付宝解析对账单失败, " + message);
            result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
            return result;
        }
        String alipayIsSuccess = doc.selectSingleNode("/alipay/is_success").getText();
        //判断请求是否成功
        if (!"T".equals(alipayIsSuccess)) {
            String errorText = doc.selectSingleNode("/alipay/error").getText();
            log.error("[validateAndParseMessage] 支付宝解析对账单返回数据状态码错误, is_success!=T, " + errorText);
            result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
            return result;
        }
        //判断是否还有下一页
        Node hasNextPage = doc.selectSingleNode("/alipay/response/account_page_query_result/has_next_page");
        result.addItem("hasNextPage", hasNextPage != null && "T".equals(hasNextPage.getText()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Node> accountLogVoList;
        Node item;
        List<OutCheckRecord> list;
        HashMap<String, List<OutCheckRecord>> records = new HashMap<>();
        records.put("在线支付", new LinkedList<OutCheckRecord>());
        records.put("转账", new LinkedList<OutCheckRecord>());
        records.put("收费", new LinkedList<OutCheckRecord>());
        records.put("提现", new LinkedList<OutCheckRecord>());
        //支付数据解析
        accountLogVoList = doc.selectNodes("/alipay/response/account_page_query_result/account_log_list/AccountQueryAccountLogVO");
        for (Node accountLogVo : accountLogVoList) {

            OutCheckRecord record = new OutCheckRecord();
            //第三方账户余额
            item = accountLogVo.selectSingleNode("balance");
            if (StringUtil.isEmpty(item.getText()))
                record.setBalance(BigDecimal.valueOf(0));
            else
                record.setBalance(BigDecimal.valueOf(Double.parseDouble(item.getText())));
            //手续费
            item = accountLogVo.selectSingleNode("service_fee");
            if (StringUtil.isEmpty(item.getText()))
                record.setCommssionFee(BigDecimal.valueOf(0));
            else
                record.setCommssionFee(BigDecimal.valueOf(Double.parseDouble(item.getText())));
            //交易金额
            item = accountLogVo.selectSingleNode("income");
            if (StringUtil.isEmpty(item.getText()))
                record.setMoney(BigDecimal.valueOf(0));
            else
                record.setMoney(BigDecimal.valueOf(Double.parseDouble(item.getText())));
            //退款金额
            item = accountLogVo.selectSingleNode("outcome");
            if (!StringUtil.isEmpty(item.getText()))
                record.setMoney(BigDecimal.valueOf(Double.parseDouble(item.getText())));
            //我方订单号
            item = accountLogVo.selectSingleNode("merchant_out_order_no");
            record.setPayNo(item.getText());
            //支付宝交易号
            item = accountLogVo.selectSingleNode("trade_no");
            record.setOutPayNo(item.getText());
            //交易完成时间
            item = accountLogVo.selectSingleNode("trans_date");
            try {
                Date date = simpleDateFormat.parse(item.getText());
                record.setOutTransTime(date);
            } catch (Exception e) {
                log.error("[validateAndParseMessage] 支付宝解析对账单返回数据错误, trans_date=" + item.getText());
            }
            //业务类型
            item = accountLogVo.selectSingleNode("trans_code_msg");
            list = records.get(item.getText());
            if (list == null)
                log.error("[validateAndParseMessage] 支付宝解析对账单返回数据错误, trans_code_msg=" + item.getText());
            else
                list.add(record);
        }
        result.addItem("payRecords", records.get("在线支付"));
        result.addItem("refRecords", records.get("转账"));
        result.addItem("feeRecords", records.get("收费"));
        result.addItem("cashRecords", records.get("提现"));
        return result;
    }


    public ResultMap prepareTransferInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap requestPMap = new PMap();
        requestPMap.put("service", AlipayService.ALIPAY_SERVICE_BATCH_TRANS);//接口名称
        requestPMap.put("partner", params.getString("merchantNo"));//商户号
        requestPMap.put("_input_charset", AlipayService.INPUT_CHARSET);//编码字符集
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));     //服务器异步通知页面路径
        requestPMap.put("account_name", params.getString("accountName"));       //付款账号名
        StringBuilder sb = new StringBuilder();
        List<TransferRecord> records = (List<TransferRecord>) params.get("records");
        for (int i = 0, size = records.size(); i < size; i++) {
            TransferRecord record = records.get(i);
            //流水号1^收款方账号1^收款账号姓名1^付款金额1^备注说明1
            sb.append(StringUtil.joinStrings("^", record.getSerialNumber(),
                    record.getAccountNo(), record.getAccountName(),
                    record.getTransferAmount(), record.getMemo())).append("|");
        }
        String detailData = sb.deleteCharAt(sb.length() - 1).toString();
        requestPMap.put("detail_data", detailData);       //付款详细数据
        requestPMap.put("batch_no", params.getString("serialNumber"));      //批量付款批次号
        requestPMap.put("batch_num", "1");      //付款总笔数
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        requestPMap.put("batch_fee", orderAmount);      //付款总金额
        requestPMap.put("email", params.getString("sellerEmail"));      //付款账号
        requestPMap.put("pay_date", DateUtil.format(new Date(), DateUtil.DATE_FORMAT_DAY_SHORT));      //支付日期
        if (!MapUtil.checkAllExist(requestPMap)) {
            log.error("[prepareTransferInfo] 支付宝批量付款参数错误, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_REFUND_PARAM_ERROR);
        }
        String md5securityKey = params.getString("md5securityKey");
        String
                sign =
                SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, AlipayService.INPUT_CHARSET);
        if (sign == null) {
            log.error("[prepareTransferInfo] 支付宝批量付款签名失败, 参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_REFUND_SIGN_ERROR);
        }
        requestPMap.put("sign", sign);
        requestPMap.put("sign_type", AlipayService.SIGN_TYPE);
        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;
    }

    public ResultMap getReqIDFromNotifyWebSync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        String is_succes = params.getString("is_success");//接口是否调用成功
        String out_trade_no = params.getString("out_trade_no");
        if (!"T".equals(is_succes) || out_trade_no == null) {
            log.error("[getReqIDFromNotifyWebSync] 支付宝支付同步回调提取out_trade_no失败, 参数:" + params);
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
            return result;
        }
//        String seller_id = params.getString("seller_id");
        result.addItem("reqId", out_trade_no);//商户网站唯一订单号
//        result.addItem("merchantNo", seller_id);//商户号
        return result;
    }

    public ResultMap getReqIDFromNotifyWebAsync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        String out_trade_no = params.getString("out_trade_no");
        if (out_trade_no == null) {
            log.error("[getReqIDFromNotifyWebAsync] 支付宝支付异步回调提取out_trade_no失败, 参数:" + params);
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
            return result;
        }
//        String seller_id = params.getString("seller_id");
        result.addItem("reqId", out_trade_no);//商户网站唯一订单号
//        result.addItem("merchantNo", seller_id);//商户号
        return result;
    }

    public ResultMap getReqIDFromNotifyWapSync(PMap params) throws ServiceException {
        return getReqIDFromNotifyWebSync(params);
    }

    public ResultMap getReqIDFromNotifyWapAsync(PMap params) throws ServiceException {
        return getReqIDFromNotifyWebAsync(params);
    }

    public ResultMap getReqIDFromNotifySDKAsync(PMap params) throws ServiceException {
        return getReqIDFromNotifyWebAsync(params);
    }

    public ResultMap getReqIDFromNotifyRefund(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        String batch_no = params.getString("batch_no");
        if (batch_no == null) {
            log.error("[getReqIDFromNotifyRefund] 支付宝退款回调提取batch_no失败, 参数:" + params);
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
            return result;
        }
        result.addItem("reqId", batch_no);//商户网站唯一订单号
        return result;
    }

    public ResultMap getReqIDFromNotifyTransfer(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        String batch_no = params.getString("batch_no");
        if (batch_no == null) {
            log.error("[getReqIDFromNotifyTransfer] 支付宝转账回调提取batch_no失败, 参数:" + params);
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
            return result;
        }
//        String pay_user_id = params.getString("pay_user_id");
        result.addItem("reqId", batch_no);//商户网站唯一订单号
//        result.addItem("merchantNo", pay_user_id);//商户号
        return result;
    }

    public ResultMap handleNotifyWebSync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap notifyParams = params.getPMap("data");
        //校验签名
        String md5securityKey = params.getString("md5securityKey");
        String out_sign = notifyParams.getString("sign");
        if (!SecretKeyUtil.aliMD5CheckSign(notifyParams, md5securityKey, out_sign, AlipayService.INPUT_CHARSET)) {
            log.error("[handleNotifyWebSync] 支付宝支付同步回调校验签名失败, 参数:" + params);
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
            return result;
        }
        //提取关键参数
        String out_trade_no = notifyParams.getString("out_trade_no");
        String trade_status = getTradeStatus(notifyParams.getString("trade_status"));

        result.addItem("reqId", out_trade_no);//商户网站唯一订单号
        result.addItem("tradeStatus", trade_status);//交易状态

        return result;
    }

    public ResultMap handleNotifyWebAsync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap notifyParams = params.getPMap("data");
        //校验签名
        String md5securityKey = params.getString("md5securityKey");
        String out_sign = notifyParams.getString("sign");
        if (!SecretKeyUtil.aliMD5CheckSign(notifyParams, md5securityKey, out_sign, AlipayService.INPUT_CHARSET)) {
            log.error("[handleNotifyWebAsync] 支付宝支付异步回调校验签名失败, 参数:" + params);
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
            return result;
        }
        //提取关键参数
        String out_trade_no = notifyParams.getString("out_trade_no");
        String trade_no = notifyParams.getString("trade_no");
        String trade_status = getTradeStatus(notifyParams.getString("trade_status"));
        String out_channel_type = notifyParams.getString("out_channel_type");
        String gmt_payment = notifyParams.getString("gmt_payment");
        String total_fee = notifyParams.getString("total_fee");

        result.addItem("reqId", out_trade_no);//商户网站唯一订单号
        result.addItem("agencyOrderId", trade_no);//第三方订单号
        result.addItem("tradeStatus", trade_status);//交易状态
        result.addItem("channelType", out_channel_type);//第三方渠道方式
        result.addItem("agencyPayTime", gmt_payment);//第三方支付时间
        result.addItem("trueMoney", total_fee);//支付金额

        return result;
    }

    public ResultMap handleNotifyWapSync(PMap params) throws ServiceException {
        return handleNotifyWebSync(params);
    }

    public ResultMap handleNotifyWapAsync(PMap params) throws ServiceException {
        return handleNotifyWebAsync(params);
    }

    public ResultMap handleNotifySDKAsync(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap notifyParams = params.getPMap("data");
        //校验签名
        String publicCertFilePath = params.getString("publicCertFilePath");
        // 3.获取商户私钥
        String privateCertKey = SecretKeyUtil.loadKeyFromFile(publicCertFilePath);
        if (privateCertKey.equals("")) {
            log.error("[handleNotifySDKAsync] 支付宝支付异步回调获取支付宝公钥失败, 参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_PAY_GET_KEY_ERROR);
        }
        String out_sign = notifyParams.getString("sign");
        if (!SecretKeyUtil.aliRSACheckSign(notifyParams, out_sign, privateCertKey)) {
            log.error("[handleNotifySDKAsync] 支付宝支付异步回调校验签名失败, 参数:" + params);
            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
            return result;
        }
        //提取关键参数
        String out_trade_no = notifyParams.getString("out_trade_no");
        String trade_no = notifyParams.getString("trade_no");
        String trade_status = getTradeStatus(notifyParams.getString("trade_status"));
        String gmt_payment = notifyParams.getString("gmt_payment");
        String total_fee = notifyParams.getString("total_fee");

        result.addItem("reqId", out_trade_no);//商户网站唯一订单号
        result.addItem("agencyOrderId", trade_no);//第三方订单号
        result.addItem("tradeStatus", trade_status);//交易状态
        result.addItem("agencyPayTime", gmt_payment);//第三方支付时间
        result.addItem("trueMoney", total_fee);//支付金额

        return result;
    }

    public ResultMap handleNotifyRefund(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap notifyParams = params.getPMap("data");
        //校验签名
        String md5securityKey = params.getString("md5securityKey");
        String out_sign = notifyParams.getString("sign");
        if (!SecretKeyUtil.aliMD5CheckSign(notifyParams, md5securityKey, out_sign, AlipayService.INPUT_CHARSET)) {
            log.error("[handleNotifyRefund] 支付宝退款异步回调校验签名失败, 参数:" + params);
            result.withError(ResultStatus.THIRD_NOTIFY_REFUND_SIGN_ERROR);
            return result;
        }

        //提取关键参数
        String batch_no = notifyParams.getString("batch_no");
        String result_details = notifyParams.getString("result_details");
        String []refund_details = result_details.split("#");
        if(refund_details.length != 1){
            log.error("[handleNotifyRefund] 支付宝退款异步回调参数错误, result_details包含多个对款记录, 参数:" + notifyParams);
            result.withError(ResultStatus.THIRD_NOTIFY_REFUND_PARAM_ERROR);
            return result;
        }
        String refund_detail = refund_details[0].split("\\$")[0];
        String []refund_detail_items = refund_detail.split("\\^");
        if(refund_detail_items.length != 3){
            log.error("[handleNotifyRefund] 支付宝退款异步回调参数错误, 参数:" + notifyParams);
            result.withError(ResultStatus.THIRD_NOTIFY_REFUND_PARAM_ERROR);
            return result;
        }
        String refund_money = refund_detail_items[1];
        String refund_status = refund_detail_items[2];

        result.addItem("reqId", batch_no);//退款单号
        result.addItem("refundMoney", refund_money);//退款金额
        result.addItem("refundStatus", refund_status);//退款状态

        return result;
    }

    public ResultMap handleNotifyTransfer(PMap params) throws ServiceException {
        throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
    }
}
