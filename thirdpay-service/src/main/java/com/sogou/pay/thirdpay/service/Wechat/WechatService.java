package com.sogou.pay.thirdpay.service.Wechat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.client.HttpService;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.common.utils.XMLUtil;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.common.enums.OrderRefundStatus;
import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.thirdpay.biz.model.OutCheckRecord;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayUtils;
import com.sogou.pay.thirdpay.service.ThirdpayService;
import org.apache.commons.codec.binary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by xiepeidong on 2016/1/27.
 */
@Service
public class WechatService implements ThirdpayService {
  private static final Logger log = LoggerFactory.getLogger(WechatService.class);

  /**
   * 微信支付参数
   */
  //币种类别
  public static final String FEE_TYPE = "CNY";       //币种
  public static final String INPUT_CHARSET = "UTF-8";    // 字符编码格式
  public static final String QR_TRADE_TYPE = "NATIVE";//扫码交易类型
  public static final String SDK_TRADE_TYPE = "APP";//SDK交易类型

  private static HashMap<String, String> TRADE_STATUS = new HashMap<String, String>();
  private static HashMap<String, String> REFUND_STATUS = new HashMap<String, String>();

  static {
    TRADE_STATUS.put("SUCCESS", OrderStatus.SUCCESS.name());//支付完成
    TRADE_STATUS.put("NOTPAY", OrderStatus.NOTPAY.name());//未支付
    TRADE_STATUS.put("CLOSED", OrderStatus.CLOSED.name());//已关闭
    TRADE_STATUS.put("REVOKED", OrderStatus.CLOSED.name());//已关闭
    TRADE_STATUS.put("USERPAYING", OrderStatus.USERPAYING.name());//支付中
    TRADE_STATUS.put("PAYERROR", OrderStatus.FAILURE.name());//支付失败
    TRADE_STATUS.put("REFUND", OrderStatus.REFUND.name());//转入退款
    TRADE_STATUS.put("DEFAULT", OrderStatus.FAILURE.name());//默认

    REFUND_STATUS.put("SUCCESS", OrderRefundStatus.SUCCESS.name());
    REFUND_STATUS.put("FAIL", OrderRefundStatus.FAIL.name());
    REFUND_STATUS.put("PROCESSING", OrderRefundStatus.PROCESSING.name());
    REFUND_STATUS.put("NOTSURE", OrderRefundStatus.UNKNOWN.name());
    REFUND_STATUS.put("CHANGE", OrderRefundStatus.OFFLINE.name());
    REFUND_STATUS.put("DEFAULT", OrderRefundStatus.UNKNOWN.name());//默认
  }


  private ResultMap prepay(PMap params, String trade_type) {
    ResultMap result = ResultMap.build();
    //1.组装签名参数
    PMap requestPMap = new PMap();
    requestPMap.put("appid", params.getString("sellerEmail"));          // 公众账号ID
    requestPMap.put("mch_id", params.getString("merchantNo"));          // 商户号
    requestPMap.put("nonce_str", TenpayUtils.getNonceStr());                  // 随机字符串，不长于32位
    requestPMap.put("body", params.getString("subject"));               // 商品描述
    requestPMap.put("out_trade_no", params.getString("serialNumber"));  //订单号
    requestPMap.put("fee_type", WechatService.FEE_TYPE);                //支付币种
    String orderAmount = TenpayUtils.fenParseFromYuan(params.getString("orderAmount"));
    requestPMap.put("total_fee", orderAmount);                          //总金额
    requestPMap.put("spbill_create_ip", "127.0.0.1");   //买家IP
//      requestPMap.put("spbill_create_ip", params.getString("buyerIp"));   //买家IP
    requestPMap.put("notify_url", params.getString("serverNotifyUrl")); //异步回调地址
    requestPMap.put("trade_type", trade_type);            //交易类型
    if (trade_type.equals(WechatService.QR_TRADE_TYPE))
      requestPMap.put("product_id", params.getString("serialNumber"));
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[prepay] 微信订单支付参数错误, 参数:" + requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PAY_PARAM_ERROR);
    }
    //2.计算md5签名
    String md5securityKey = params.getString("md5securityKey");
    String sign =
            SecretKeyUtil
                    .tenMD5Sign(requestPMap, md5securityKey, WechatService.INPUT_CHARSET);
    if (sign == null) {
      log.error("[prepay] 微信订单支付签名失败, 参数:" + requestPMap);
      result.withError(ResultStatus.THIRD_PAY_SIGN_ERROR);
      return result;
    }

    requestPMap.put("sign", sign);
    String paramsStr = XMLUtil.Map2XML("xml", requestPMap);
    //3.模拟请求获取支付回调参数
    Result httpResponse = HttpService.getInstance().doPost(params.getString("prepayUrl"), paramsStr, WechatService.INPUT_CHARSET, null);
    if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
      log.error("[prepay] 微信订单支付HTTP请求失败, 参数:" + requestPMap);
      result.withError(ResultStatus.THIRD_PAY_HTTP_ERROR);
      return result;
    }
    String resContent = (String) httpResponse.getReturnValue();
    PMap prepayPMap = null;
    try {
      prepayPMap = XMLUtil.XML2PMap(resContent);
    } catch (Exception e) {
      log.error("[prepay] 微信订单支付解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
      result.withError(ResultStatus.THIRD_PAY_XML_PARSE_ERROR);
      return result;
    }
    if (prepayPMap == null) {
      log.error("[queryOrder] 微信订单支付解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
      result.withError(ResultStatus.THIRD_PAY_XML_PARSE_ERROR);
      return result;
    }

    //4.检查返回参数
    if (StringUtil.isEmpty(prepayPMap.getString("return_code"), prepayPMap.getString("result_code"),
            prepayPMap.getString("sign"))) {
      log.error("[prepay] 微信订单支付返回参数异常, 参数:" + requestPMap + ", 返回:" + prepayPMap);
      result.withError(ResultStatus.THIRD_PAY_RESPONSE_PARAM_ERROR);
      return result;
    }

    //5.签名校验
    boolean
            signMd5 =
            SecretKeyUtil.tenMD5CheckSign(prepayPMap, md5securityKey, prepayPMap.getString("sign"),
                    WechatService.INPUT_CHARSET);
    if (!signMd5) {
      log.error("[prepay] 微信订单支付返回参数签名错误, 参数:" + prepayPMap);
      result.withError(ResultStatus.THIRD_PAY_RESPONSE_SIGN_ERROR);
      return result;
    }
    String return_trade_type = prepayPMap.getString("trade_type");
    if (!return_trade_type.equals(trade_type)) {
      log.error("[prepay] 微信订单支付返回参数异常, trade_type不相符, 参数:" + requestPMap + ", 返回:" + prepayPMap);
      result.withError(ResultStatus.THIRD_PAY_RESPONSE_PARAM_ERROR);
      return result;
    }
    result.withReturn(prepayPMap);
    return result;
  }


  @Override
  public ResultMap preparePayInfoAccount(PMap params) throws ServiceException {
    return null;
  }

  @Override
  public ResultMap preparePayInfoGatway(PMap params) throws ServiceException {
    return null;
  }

  @Override
  public ResultMap preparePayInfoQRCode(PMap params) throws ServiceException {
    ResultMap result = prepay(params, WechatService.QR_TRADE_TYPE);
    if (result.getStatus() != ResultStatus.SUCCESS) {
      return result;
    }
    PMap prepayPMap = (PMap) result.getReturnValue();
    //返回二维码图片数据
    result.addItem("qrCode", text2QRCode(prepayPMap.getString("code_url")));
    return result;
  }

  @Override
  public ResultMap preparePayInfoSDK(PMap params) throws ServiceException {
    ResultMap result = prepay(params, WechatService.SDK_TRADE_TYPE);
    if (result.getStatus() != ResultStatus.SUCCESS) {
      return result;
    }
    PMap prepayPMap = (PMap) result.getReturnValue();
    //5.组装发往商户参数
    PMap requestPMap = new PMap();
    requestPMap.put("appid", params.getString("sellerEmail"));
    requestPMap.put("partnerid", params.getString("merchantNo"));
    requestPMap.put("prepayid", prepayPMap.get("prepay_id"));
    requestPMap.put("package", "Sign=WXPay");
    requestPMap.put("noncestr", TenpayUtils.getNonceStr());
    requestPMap.put("timestamp", TenpayUtils.getTimeStamp());
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[preparePayInfoSDK] 微信订单支付参数错误, 参数:" + requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PAY_PARAM_ERROR);
    }
    String md5securityKey = params.getString("md5securityKey");
    String signMd5 =
            SecretKeyUtil
                    .tenMD5Sign(requestPMap, md5securityKey, WechatService.INPUT_CHARSET);
    if (signMd5 == null) {
      log.error("[preparePayInfoSDK] 微信订单支付签名失败, 参数:" + requestPMap);
      result.withError(ResultStatus.THIRD_PAY_SIGN_ERROR);
      return result;
    }
    requestPMap.put("sign", signMd5);
    result.addItem("orderInfo", requestPMap);
    return result;
  }

  @Override
  public ResultMap preparePayInfoWap(PMap params) throws ServiceException {
    return null;
  }

  /**
   * 微信查询订单信息
   * <p/>
   * 只能查询半年内的订单, 超过半年的订单调用此查询接口会报“88221009交易单不存在”
   */
  @Override
  public ResultMap queryOrder(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    PMap requestPMap = new PMap();
    //1拼装请求参数
    requestPMap.put("appid", params.getString("sellerEmail"));          // 公众账号ID
    requestPMap.put("mch_id", params.getString("merchantNo"));          // 商户号
    requestPMap.put("nonce_str", TenpayUtils.getNonceStr());                  // 随机字符串，不长于32位
    requestPMap.put("out_trade_no", params.getString("serialNumber"));  //商户订单号
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[queryOrder] 微信订单查询参数错误, 参数:" + requestPMap);
      return ResultMap.build(ResultStatus.THIRD_QUERY_PARAM_ERROR);
    }
    //2.获得密钥，MD5签名
    String md5securityKey = params.getString("md5securityKey");
    String sign =
            SecretKeyUtil
                    .tenMD5Sign(requestPMap, md5securityKey, WechatService.INPUT_CHARSET);
    if (sign == null) {
      log.error("[queryOrder] 微信订单查询签名失败, 参数:" + requestPMap);
      result.withError(ResultStatus.THIRD_QUERY_SIGN_ERROR);
      return result;
    }
    requestPMap.put("sign", sign);
    String paramsStr = XMLUtil.Map2XML("xml", requestPMap);
    //3.模拟请求，获取查询参数
    Result httpResponse = HttpService.getInstance().doPost(params.getString("queryUrl"), paramsStr, WechatService.INPUT_CHARSET, null);
    if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
      log.error("[queryOrder] 微信订单查询HTTP请求失败, 参数:" + requestPMap);
      result.withError(ResultStatus.THIRD_QUERY_HTTP_ERROR);
      return result;
    }
    String resContent = (String) httpResponse.getReturnValue();
    PMap orderPMap = null;
    try {
      orderPMap = XMLUtil.XML2PMap(resContent);
    } catch (Exception e) {
      log.error("[queryOrder] 微信订单查询解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
      result.withError(ResultStatus.THIRD_QUERY_XML_PARSE_ERROR);
      return result;
    }
    if (orderPMap == null) {
      log.error("[queryOrder] 微信订单查询解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
      result.withError(ResultStatus.THIRD_QUERY_XML_PARSE_ERROR);
      return result;
    }

    //4.检查返回参数
    if (StringUtil.isEmpty(orderPMap.getString("return_code"), orderPMap.getString("result_code"),
            orderPMap.getString("sign"), orderPMap.getString("trade_state"))) {
      log.error("[queryOrder] 微信订单查询返回参数异常, 参数:" + requestPMap + ", 返回:" + orderPMap);
      result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
      return result;

    }
    //5.签名校验
    boolean
            signMd5 =
            SecretKeyUtil.tenMD5CheckSign(orderPMap, md5securityKey, orderPMap.getString("sign"),
                    WechatService.INPUT_CHARSET);
    if (!signMd5) {
      log.error("[queryOrder] 微信订单查询返回参数签名错误, 参数:" + orderPMap);
      result.withError(ResultStatus.THIRD_QUERY_RESPONSE_SIGN_ERROR);
      return result;
    }
    //6.返回交易状态
    result.addItem("order_state", getTradeStatus(orderPMap.getString("trade_state").toUpperCase()));
    return result;
  }

  private String getTradeStatus(String wechatTradeStatus) {
    if (wechatTradeStatus == null) return WechatService.TRADE_STATUS.get("DEFAULT");
    String trade_status = WechatService.TRADE_STATUS.get(wechatTradeStatus);
    if (trade_status == null) return WechatService.TRADE_STATUS.get("DEFAULT");
    return trade_status;
  }

  private String getRefundStatus(String wechatRefundStatus) {
    if (wechatRefundStatus == null) return WechatService.REFUND_STATUS.get("DEFAULT");
    String refund_status = WechatService.REFUND_STATUS.get(wechatRefundStatus);
    if (refund_status == null) return WechatService.REFUND_STATUS.get("DEFAULT");
    return refund_status;
  }

  /**
   * 微信订单退款
   * 只能退半年内的订单, 超过半年的订单调用此退款接口会报“88221009交易单不存在”
   */
  @Override
  public ResultMap refundOrder(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    PMap requestPMap = new PMap();
    //1.组装请求参数
    requestPMap.put("appid", params.getString("sellerEmail"));          // 公众账号ID
    requestPMap.put("mch_id", params.getString("merchantNo"));          // 商户号
    requestPMap.put("nonce_str", TenpayUtils.getNonceStr());                  // 随机字符串，不长于32位
    requestPMap.put("transaction_id", params.getString("agencySerialNumber")); //微信订单号
    requestPMap.put("out_trade_no", params.getString("serialNumber"));  //订单号
    requestPMap.put("out_refund_no", params.getString("refundSerialNumber"));  //商户退款号
    String total_fee = TenpayUtils.fenParseFromYuan(params.getString("totalAmount"));
    requestPMap.put("total_fee", total_fee);                          //总金额
    String refundAmount = TenpayUtils.fenParseFromYuan(params.getString("refundAmount"));
    requestPMap.put("refund_fee", refundAmount);                          //退款金额
    requestPMap.put("refund_fee_type", WechatService.FEE_TYPE);   //货币种类
    requestPMap.put("op_user_id", params.getString("merchantNo"));   //操作员
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[refundOrder] 微信退款参数错误, 参数:" + requestPMap);
      return ResultMap.build(ResultStatus.THIRD_REFUND_PARAM_ERROR);
    }
    //2.计算md5签名
    String md5securityKey = params.getString("md5securityKey");        // 加密秘钥
    String sign =
            SecretKeyUtil.tenMD5Sign(requestPMap, md5securityKey, WechatService.INPUT_CHARSET);
    if (sign == null) {
      log.error("[refundOrder] 微信退款签名失败，参数：" + requestPMap);
      result.withError(ResultStatus.THIRD_REFUND_SIGN_ERROR);
      return result;
    }
    requestPMap.put("sign", sign); // 密钥
    String paramsStr = XMLUtil.Map2XML("xml", requestPMap);
    //3.发送退款请求
    WechatHttpClient httpClient = new WechatHttpClient();
    httpClient.setCertFile("e:"+params.getString("privateCertFilePath"), params.getString("merchantNo"), "e:"+params.getString("publicCertFilePath"));
    Result httpResponse = httpClient.doPost(params.getString("refundUrl"), paramsStr);
    if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
      log.error("[refundOrder] 微信退款HTTP请求失败, 参数:" + requestPMap);
      result.withError(ResultStatus.THIRD_REFUND_HTTP_ERROR);
      return result;
    }
    //4.解析退款结果
    String resContent = (String) httpResponse.getReturnValue();
    PMap responsePMap = null;
    try {
      responsePMap = XMLUtil.XML2PMap(resContent);
    } catch (Exception ex) {
      log.error("[refundOrder] 微信退款解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
      result.withError(ResultStatus.THIRD_REFUND_XML_PARSE_ERROR);
      return result;
    }
    if (responsePMap == null) {
      log.error("[refundOrder] 微信退款解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
      result.withError(ResultStatus.THIRD_REFUND_XML_PARSE_ERROR);
      return result;
    }
    String return_code = responsePMap.getString("return_code");
    if (StringUtil.isEmpty(return_code) || !"SUCCESS".equals(return_code)) {
      log.error("[refundOrder] 微信退款返回参数异常, return_code!=SUCCESS, 参数:" + requestPMap + ", 返回:" + resContent);
      result.addItem("error_code", return_code);
      result.addItem("error_msg", responsePMap.getString("return_msg"));
      result.withError(ResultStatus.THIRD_REFUND_RESPONSE_PARAM_ERROR);
      return result;
    }
    //6.签名校验
    boolean
            signMd5 =
            SecretKeyUtil
                    .tenMD5CheckSign(responsePMap, md5securityKey, responsePMap.getString("sign"),
                            WechatService.INPUT_CHARSET);
    if (!signMd5) {
      log.error("[refundOrder] 微信退款返回参数签名错误, 参数:" + responsePMap);
      result.withError(ResultStatus.THIRD_REFUND_RESPONSE_SIGN_ERROR);
      return result;
    }
    String result_code = responsePMap.getString("result_code");
    if (StringUtil.isEmpty(result_code) || !"SUCCESS".equals(result_code)) {
      log.error("[refundOrder] 微信退款返回参数异常, result_code!=SUCCESS，参数:" + requestPMap + ", 返回:" + responsePMap);
      result.addItem("error_code", responsePMap.getString("err_code"));
      result.addItem("error_msg", responsePMap.getString("err_code_des"));
      result.withError(ResultStatus.THIRD_REFUND_RESPONSE_PARAM_ERROR);
      return result;
    }
    //7.返回退款结果
    return result.addItem("third_refund_id", responsePMap.getString("refund_id"));
  }


  /**
   * 微信查询订单退款信息
   * <p/>
   * 只能查询半年内的订单, 超过半年的订单调用此查询接口会报“88221009交易单不存在”
   */
  @Override
  public ResultMap queryRefundOrder(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    //1.组装请求参数
    PMap requestPMap = new PMap();
    requestPMap.put("appid", params.getString("sellerEmail"));          // 公众账号ID
    requestPMap.put("mch_id", params.getString("merchantNo"));          // 商户号
    requestPMap.put("nonce_str", TenpayUtils.getNonceStr());                  // 随机字符串，不长于32位
    requestPMap.put("out_refund_no", params.getString("out_refund_no"));  //商户退款号
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[queryRefundOrder] 微信退款查询参数错误, 参数:" + requestPMap);
      return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_PARAM_ERROR);
    }
    //2.计算md5签名
    String md5securityKey = params.getString("md5securityKey");        // 加密秘钥
    String sign = SecretKeyUtil
            .tenMD5Sign(requestPMap, md5securityKey, WechatService.INPUT_CHARSET);
    if (sign == null) {
      log.error("[queryRefundOrder] 微信退款查询签名失败, 参数:" + requestPMap);
      return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_SIGN_ERROR);
    }
    requestPMap.put("sign", sign);
    String paramsStr = XMLUtil.Map2XML("xml", requestPMap);
    //3.发送查询请求
    Result httpResponse = HttpService.getInstance().doPost(params.getString("queryRefundUrl"), paramsStr, WechatService.INPUT_CHARSET, null);
    if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
      log.error("[queryRefundOrder] 微信退款查询HTTP请求失败, 参数:" + requestPMap);
      result.withError(ResultStatus.THIRD_QUERY_REFUND_HTTP_ERROR);
      return result;
    }
    //4.解析查询结果
    String resContent = (String) httpResponse.getReturnValue();
    PMap orderPMap = null;
    try {
      orderPMap = XMLUtil.XML2PMap(resContent);
    } catch (Exception e) {
      log.error("[queryRefundOrder] 微信退款查询解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
      result.withError(ResultStatus.THIRD_QUERY_REFUND_XML_PARSE_ERROR);
      return result;
    }
    if (orderPMap == null) {
      log.error("[queryRefundOrder] 微信退款查询解析响应报文异常, 参数:" + requestPMap + ", 返回:" + resContent);
      result.withError(ResultStatus.THIRD_QUERY_REFUND_XML_PARSE_ERROR);
      return result;
    }

    //4.检查返回参数
    if (StringUtil.isEmpty(orderPMap.getString("result_code"), orderPMap.getString("sign"))) {
      log.error("[queryRefundOrder] 微信退款查询返回参数异常, 参数:" + requestPMap + ", 返回:" + resContent);
      return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_RESPONSE_PARAM_ERROR);
    }
    //5.校验签名
    boolean
            signMd5 =
            SecretKeyUtil.tenMD5CheckSign(orderPMap, md5securityKey, orderPMap.getString("sign"),
                    WechatService.INPUT_CHARSET);
    if (!signMd5) {
      log.error("[queryRefundOrder] 微信退款查询返回参数签名异常, 参数:" + requestPMap + ", 返回:" + resContent);
      return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_RESPONSE_SIGN_ERROR);
    }
    //6.返回交易状态
    result.addItem("refund_status", getRefundStatus(orderPMap.getString("refund_status_0").toUpperCase()));
    return result;
  }

  @Override
  public ResultMap downloadOrder(PMap params) throws ServiceException {

    ResultMap result = ResultMap.build();

    Date checkDate = (Date) params.get("checkDate");
    String wechatCheckDate = DateUtil.format(checkDate, DateUtil.DATE_FORMAT_DAY_SHORT);

    PMap requestPMap = new PMap();
    requestPMap.put("appid", params.getString("sellerEmail"));//公众账号ID
    requestPMap.put("mch_id", params.getString("merchantNo"));//商户号
    requestPMap.put("nonce_str", TenpayUtils.getNonceStr()); //随机32位字符串
    requestPMap.put("bill_date", wechatCheckDate);//对账单日起
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[downloadOrder] 微信下载对账单参数错误, 参数:" + requestPMap);
      return ResultMap.build(ResultStatus.THIRD_QUERY_PARAM_ERROR);
    }
    CheckType checkType = (CheckType) params.get("checkType");
    if (checkType == CheckType.ALL) {
      // 全部订单
      requestPMap.put("bill_type", "ALL");
    } /*else if (checkType == CheckType.PAID) {
            // 成功支付的订单
            requestPMap.put("bill_type", "SUCCESS");
        } else if (checkType == CheckType.REFUND) {
            // 退款订单
            requestPMap.put("bill_type", "REFUND");
        }*/ else {
      log.error("[downloadOrder] 微信下载对账单参数错误, 参数:" + params);
      result.withError(ResultStatus.THIRD_QUERY_PARAM_ERROR);
      return result;
    }
    //获取md5签名
    String key = params.getString("key");
    String sign = SecretKeyUtil.tenMD5Sign(requestPMap, key, WechatService.INPUT_CHARSET);
    if (sign == null) {
      log.error("[downloadOrder] 微信下载对账单签名失败, 参数:" + requestPMap);
      result.withError(ResultStatus.THIRD_QUERY_SIGN_ERROR);
      return result;
    }
    requestPMap.put("sign", sign);
    //将请求参数转换成 xml 数据
    String paramsStr = XMLUtil.Map2XML("xml", requestPMap);
    log.info(paramsStr);
    //3.发送查询请求
    Result httpResponse = HttpService.getInstance().doPost(params.getString("downloadUrl"), paramsStr, WechatService.INPUT_CHARSET, null);
    if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
      log.error("[downloadOrder] 微信下载对账单HTTP请求失败, 参数:" + requestPMap);
      result.withError(ResultStatus.THIRD_QUERY_HTTP_ERROR);
      return result;
    }
    //4.解析查询结果
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

      if (message.startsWith("<xml>")) {
        PMap pMap = XMLUtil.XML2PMap(message);
        String errorText = String.valueOf(pMap.get("return_msg"));
        log.error("[validateAndParseMessage] 微信解析对账单返回参数异常, 返回:" + message);
        //没有对账单数据
        if ("No Bill Exist".equals(errorText)) {
          return result;
        }
        result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        return result;
      }

      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      reader = new BufferedReader(new StringReader(message));
      line = reader.readLine();// 第一行是表头，忽略
      if (line == null) {
        log.error("[validateAndParseMessage] 微信解析对账单返回参数异常, 返回:" + message);
        result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        return result;
      }
      while ((line = reader.readLine()) != null) {
        //汇总标题 结束
        if (!line.startsWith("`")) {
          break;
        }
        OutCheckRecord record = new OutCheckRecord();
        String[] parts = line.split(",");
        String trade_status = parts[9].trim().replaceFirst("`", "");
        //第三方交易时间
        record.setOutTransTime(df.parse(parts[0].trim().replaceFirst("`", "")));
        //手续费
        BigDecimal commssionFee = BigDecimal.valueOf(Double.parseDouble(parts[22].trim().replaceFirst("`", "")));
        record.setCommssionFee(commssionFee);
        if (trade_status.equals("SUCCESS")) {
          //第三方订单号
          record.setOutPayNo(parts[5].trim().replaceFirst("`", ""));
          //我方单号
          record.setPayNo(parts[6].trim().replaceFirst("`", ""));
          //交易金额
          BigDecimal money = BigDecimal.valueOf(Double.parseDouble(parts[12].trim().replaceFirst("`", "")));
          record.setMoney(money);
          payRecords.add(record);
        } else if (trade_status.equals("REFUND")) {
          //第三方退款单号
          record.setOutPayNo(parts[14].trim().replaceFirst("`", ""));
          //我方退款单号
          record.setPayNo(parts[15].trim().replaceFirst("`", ""));
          //退款金额
          BigDecimal money = BigDecimal.valueOf(Double.parseDouble(parts[16].trim().replaceFirst("`", "")));
          record.setMoney(money);
          refRecords.add(record);
        }
      }
      result.addItem("payRecords", payRecords);
      result.addItem("refRecords", refRecords);
    } catch (Exception e) {
      e.printStackTrace();
      log.error("[validateAndParseMessage] 微信解析对账单返回参数异常, 返回:" + line);
      result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
    }
    return result;
  }

  @Override
  public ResultMap prepareTransferInfo(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  //实际上，微信扫码支付并没有同步通知，是由搜狗支付中心页面模拟发起的同步通知
  public ResultMap getReqIDFromNotifyWebSync(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    String out_trade_no = params.getString("out_trade_no");
    if (out_trade_no == null) {
      log.error("[getReqIDFromNotifyWebSync] 微信扫码支付同步回调提取out_trade_no失败, 参数:" + params);
      result.withError(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
      return result;
    }
    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
    return result;
  }

  public ResultMap getReqIDFromNotifyWebAsync(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    String return_code = params.getString("return_code");
    String out_trade_no = params.getString("out_trade_no");
    if (!return_code.equals("SUCCESS") || out_trade_no == null) {
      log.error("[getReqIDFromNotifyWebAsync] 微信扫码支付异步回调提取out_trade_no失败, 参数:" + params);
      result.withError(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
      return result;
    }
//        String mch_id = params.getString("mch_id");
    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
//        result.addItem("merchantNo", mch_id);//商户号
    return result;
  }

  public ResultMap getReqIDFromNotifyWapSync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifyWapAsync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifySDKAsync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifyRefund(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifyTransfer(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  //实际上，微信扫码支付并没有同步通知，是由搜狗支付中心页面模拟发起的同步通知
  public ResultMap handleNotifyWebSync(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    PMap notifyParams = params.getPMap("data");
    //校验签名
//        String md5securityKey = params.getString("md5securityKey");
//        String out_sign = notifyParams.getString("sign");
//        if (!SecretKeyUtil.tenMD5CheckSign(notifyParams, md5securityKey, out_sign, WechatService.INPUT_CHARSET)) {
//            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
//            return result;
//        }
    //提取关键参数
    String out_trade_no = notifyParams.getString("out_trade_no");
    String result_code = getTradeStatus(notifyParams.getString("result_code"));

    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
    result.addItem("tradeStatus", result_code);//交易状态

    return result;
  }

  public ResultMap handleNotifyWebAsync(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    PMap notifyParams = params.getPMap("data");
    //校验签名
    String md5securityKey = params.getString("md5securityKey");
    String out_sign = notifyParams.getString("sign");
    if (!SecretKeyUtil.tenMD5CheckSign(notifyParams, md5securityKey, out_sign, WechatService.INPUT_CHARSET)) {
      log.error("[handleNotifyWebAsync] 微信扫码支付异步回调校验签名失败, 参数:" + params);
      result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
      return result;
    }
    //提取关键参数
    String out_trade_no = notifyParams.getString("out_trade_no");
    String transaction_id = notifyParams.getString("transaction_id");
    String result_code = getTradeStatus(notifyParams.getString("result_code"));
    String time_end = notifyParams.getString("time_end");
    String total_fee = notifyParams.getString("total_fee");
    total_fee = String.valueOf(new BigDecimal(total_fee).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UP));

    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
    result.addItem("agencyOrderId", transaction_id);//第三方订单号
    result.addItem("tradeStatus", result_code);//交易状态
    result.addItem("agencyPayTime", time_end);//第三方支付时间
    result.addItem("trueMoney", total_fee);//支付金额

    return result;
  }

  public ResultMap handleNotifyWapSync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap handleNotifyWapAsync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap handleNotifySDKAsync(PMap params) throws ServiceException {
    return handleNotifyWebAsync(params);
  }

  public ResultMap handleNotifyRefund(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap handleNotifyTransfer(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  private String text2QRCode(String content) throws ServiceException {
    try {
      Map<EncodeHintType, String> hints = new HashMap<>();
      hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
      BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
      BufferedImage
              qrcodeImg =
              MatrixToImageWriter.toBufferedImage(bitMatrix);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(qrcodeImg, "PNG", baos);
      baos.close();
      String base64 = org.apache.commons.codec.binary.Base64.encodeBase64String(baos.toByteArray());
      base64 = URLEncoder.encode(base64, "UTF-8");
      return String.format("data:image/png;base64,%s", base64);
    } catch (Exception e) {
      log.error("[text2QRCode] failed, params={}, {}", content, e.getStackTrace());
      throw new ServiceException(ResultStatus.THIRD_PAY_ERROR);
    }
  }
}
