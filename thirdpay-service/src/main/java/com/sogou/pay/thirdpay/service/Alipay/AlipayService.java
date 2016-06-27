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
   * 支付宝钱包支付, 调用支付宝接口名
   */
  // 支付宝钱包支付, 超时时间设置 , 默认30分钟, 一旦超时, 该笔交易就会自动被关闭, 取值范围：1m～15d。
  // m-分钟, h-小时, d-天, 1c-当天（无论交易何时创建, 都在0点关闭）。
  // 该参数数值不接受小数点, 如1.5h, 可转换为90m。
  public static final String IT_B_PAY = "30m";
  private static final Logger log = LoggerFactory.getLogger(AlipayService.class);
  /**
   * 支付宝扫码支付参数
   */
  // 1） 简约前置模式：qr_pay_mode=0;
  // 2） 前置模式：qr_pay_mode=1;
  // 3） 页面跳转模式：这个参数的值 qr_pay_mode=2 , 直接进入到支付宝收银台
  public static String QR_PAY_MODE = "0";   //扫码支付模式
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
    ResultMap result;
    //组装参数
    PMap requestPMap = new PMap();
    requestPMap.put("service", ALIPAY_SERVICE_DIRECTPAY);             //接口名称
    requestPMap.put("partner", params.getString("merchantNo"));             //合作者身份ID
    requestPMap.put("_input_charset", INPUT_CHARSET);            //参数编码
    requestPMap.put("notify_url", params.getString("serverNotifyUrl"));     //服务器异步通知页面路径
    requestPMap.put("return_url", params.getString("pageNotifyUrl"));       //页面跳转同步通知页面路径（可空）
    requestPMap.put("out_trade_no", params.getString("serialNumber"));      //商户网站唯一订单号
    requestPMap.put("subject", params.getString("subject"));                //商品名称
    requestPMap.put("payment_type", PAYMENT_TYPE);               //支付类型
    requestPMap.put("seller_id", params.getString("merchantNo"));             //卖家支付宝账户号
    BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
    String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    requestPMap.put("total_fee", orderAmount);                                    //交易金额
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[preparePayInfoAccount] request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    String md5securityKey = params.getString("md5securityKey");
    result = signMD5(requestPMap, md5securityKey);
    if (!Result.isSuccess(result)) return result;

    //生成支付URL
    String returnUrl = HttpUtil.packHttpGetUrl(params.getString("payUrl"), requestPMap);
    return ResultMap.build().addItem("returnUrl", returnUrl);
  }

  @Override
  public ResultMap preparePayInfoGatway(PMap params) throws ServiceException {
    ResultMap result;
    //组装参数
    PMap requestPMap = new PMap();
    requestPMap.put("service", ALIPAY_SERVICE_DIRECTPAY);                   //接口名称
    requestPMap.put("partner", params.getString("merchantNo"));                   //合作者身份ID
    requestPMap.put("_input_charset", INPUT_CHARSET);                  //参数编码
    requestPMap.put("notify_url", params.getString("serverNotifyUrl"));           //服务器异步通知页面路径
    requestPMap.put("return_url", params.getString("pageNotifyUrl"));             //页面跳转同步通知页面路径（可空）
    requestPMap.put("out_trade_no", params.getString("serialNumber"));            //商户网站唯一订单号
    requestPMap.put("paymethod", "bankPay");//默认支付方式
    requestPMap.put("defaultbank", params.get("bankCode"));//默认银行
    requestPMap.put("subject", params.getString("subject"));                      //商品名称
    requestPMap.put("payment_type", PAYMENT_TYPE);                     //支付类型
    requestPMap.put("seller_id", params.getString("merchantNo"));             //卖家支付宝账户号
    BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
    String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    requestPMap.put("total_fee", orderAmount);
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[preparePayInfoGatway] request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    String md5securityKey = params.getString("md5securityKey");
    result = signMD5(requestPMap, md5securityKey);
    if (!Result.isSuccess(result)) return result;

    //生成支付URL
    String returnUrl = HttpUtil.packHttpGetUrl(params.getString("payUrl"), requestPMap);
    return ResultMap.build().addItem("returnUrl", returnUrl);
  }

  @Override
  public ResultMap preparePayInfoQRCode(PMap params) throws ServiceException {
    ResultMap result;
    //组装参数
    PMap requestPMap = new PMap();
    requestPMap.put("service", ALIPAY_SERVICE_DIRECTPAY);             //接口名称
    requestPMap.put("partner", params.getString("merchantNo"));             //合作者身份ID
    requestPMap.put("_input_charset", INPUT_CHARSET);            //参数编码
    requestPMap.put("notify_url", params.getString("serverNotifyUrl"));     //服务器异步通知页面路径
    requestPMap.put("return_url", params.getString("pageNotifyUrl"));       //页面跳转同步通知页面路径（可空）
    requestPMap.put("out_trade_no", params.getString("serialNumber"));      //商户网站唯一订单号
    requestPMap.put("subject", params.getString("subject"));                //商品名称
    requestPMap.put("payment_type", PAYMENT_TYPE);               //支付类型
    requestPMap.put("seller_id", params.getString("merchantNo"));             //卖家支付宝账户号
    BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
    String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    requestPMap.put("total_fee", orderAmount);                                    //支付金额
    requestPMap.put("qr_pay_mode", QR_PAY_MODE);
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[preparePayInfoQRCode] request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    String md5securityKey = params.getString("md5securityKey");
    result = signMD5(requestPMap, md5securityKey);
    if (!Result.isSuccess(result)) return result;

    //生成支付URL
    String returnUrl = HttpUtil.packHttpGetUrl(params.getString("payUrl"), requestPMap);
    return ResultMap.build().addItem("qrCode", returnUrl);
  }

  private String packit(String s) {
    return new StringBuilder("\"").append(s).append("\"").toString();
  }

  @Override
  public ResultMap preparePayInfoSDK(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    //组装参数
    PMap requestPMap = new PMap();
    requestPMap.put("service", packit(ALIPAY_SERVICE_MOBILE_DIRECTPAY));             //接口名称
    requestPMap.put("partner", packit(params.getString("merchantNo")));             //合作者身份ID
    requestPMap.put("_input_charset", packit(INPUT_CHARSET));            //参数编码
    requestPMap.put("notify_url", packit(params.getString("serverNotifyUrl")));     //服务器异步通知页面路径
    requestPMap.put("out_trade_no", packit(params.getString("serialNumber")));      //商户网站唯一订单号
    requestPMap.put("subject", packit(params.getString("subject")));                //商品名称
    requestPMap.put("body", packit(params.getString("subject")));                //商品名称
    requestPMap.put("payment_type", packit(PAYMENT_TYPE));               //支付类型
    requestPMap.put("seller_id", packit(params.getString("merchantNo")));             //卖家支付宝账户号
    BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
    String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    requestPMap.put("total_fee", packit(orderAmount));                                    //支付金额
    requestPMap.put("it_b_pay", packit(IT_B_PAY));//未付款交易的超时时间
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[preparePayInfoSDK] request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    String privateCertFilePath = params.getString("privateCertFilePath");
    String privateCertKey = SecretKeyUtil.loadKeyFromFile(privateCertFilePath);
    if (StringUtil.isEmpty(privateCertKey)) {
      log.error("[preparePayInfoSDK] get private key failed, params={}", privateCertFilePath);
      return ResultMap.build(ResultStatus.THIRD_GET_KEY_ERROR);
    }
    StringBuilder requestString = new StringBuilder(SecretKeyUtil.buildSignSource(requestPMap));
    String sign =
            SecretKeyUtil.aliRSASign(requestString.toString(), privateCertKey);
    if (sign == null) {
      log.error("[preparePayInfoSDK] sign failed, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_SIGN_ERROR);
    }
    sign = HttpUtil.urlEncode(sign);
    requestString.append("&").append("sign").append("=").append(packit(sign));//签名
    requestString.append("&").append("sign_type").append("=").append(packit("RSA"));//签名方式
    String payInfo = requestString.toString();
    //获取客户端需要的支付宝公钥
    String publicCertFilePath = params.getString("publicCertFilePath");
    String publicCertKey = SecretKeyUtil.loadKeyFromFile(publicCertFilePath);
    if (StringUtil.isEmpty(publicCertKey)) {
      log.error("[preparePayInfoSDK] get public key failed, params={}", privateCertFilePath);
      return ResultMap.build(ResultStatus.THIRD_GET_KEY_ERROR);
    }
    result.addItem("orderInfo", payInfo);
    result.addItem("aliPublicKey", publicCertKey);
    return result;
  }

  @Override
  public ResultMap preparePayInfoWap(PMap params) throws ServiceException {
    ResultMap result;
    //组装参数
    PMap requestPMap = new PMap();
    requestPMap.put("service", ALIPAY_SERVICE_WAP_DIRECTPAY);             //接口名称
    requestPMap.put("partner", params.getString("merchantNo"));             //合作者身份ID
    requestPMap.put("_input_charset", INPUT_CHARSET);            //参数编码
    requestPMap.put("notify_url", params.getString("serverNotifyUrl"));     //服务器异步通知页面路径
    requestPMap.put("return_url", params.getString("pageNotifyUrl"));       //页面跳转同步通知页面路径（可空）
    requestPMap.put("out_trade_no", params.getString("serialNumber"));      //商户网站唯一订单号
    requestPMap.put("subject", params.getString("subject"));                //商品名称
    requestPMap.put("payment_type", PAYMENT_TYPE);               //支付类型
    requestPMap.put("seller_id", params.getString("merchantNo"));             //卖家支付宝账户号
    BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
    String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    requestPMap.put("total_fee", orderAmount);                                    //交易金额
    requestPMap.put("it_b_pay", IT_B_PAY);
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[preparePayInfoWap] request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    String md5securityKey = params.getString("md5securityKey");
    result = signMD5(requestPMap, md5securityKey);
    if (!Result.isSuccess(result)) return result;

    //生成支付URL
    String returnUrl = HttpUtil.packHttpGetUrl(params.getString("payUrl"), requestPMap);
    return ResultMap.build().addItem("returnUrl", returnUrl);
  }

  @Override
  public ResultMap queryOrder(PMap params) throws ServiceException {
    ResultMap result;
    //组装参数
    PMap requestPMap = new PMap();
    requestPMap.put("service", ALIPAY_SERVICE_QUERY);                  //查询订单接口名
    requestPMap.put("partner", params.getString("merchantNo"));                //商户号
    requestPMap.put("_input_charset", INPUT_CHARSET);               //编码
    requestPMap.put("out_trade_no", params.getString("serialNumber"));             //订单号
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[queryOrder] request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    String md5securityKey = params.getString("md5securityKey");
    result = signMD5(requestPMap, md5securityKey);
    if (!Result.isSuccess(result)) return result;

    //发起请求
    Result httpResponse = HttpService.getInstance().doPost(params.getString("queryUrl"), requestPMap, INPUT_CHARSET, null);
    if (!Result.isSuccess(httpResponse)) {
      log.error("[queryOrder] http request failed, url={}, params={}", params.getString("queryUrl"), requestPMap);
      return ResultMap.build(ResultStatus.THIRD_HTTP_ERROR);
    }
    String resContent = (String) httpResponse.getReturnValue();
    //解析响应
    PMap alipayMap;
    try {
      alipayMap = XMLUtil.XML2PMap(resContent);
    } catch (Exception e) {
      log.error("[queryOrder] response error, request={}, response={}", requestPMap, resContent);
      throw new ServiceException(e, ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    String alipayIsSuccess = alipayMap.getString("is_success");
    if (!"T".equals(alipayIsSuccess)) {
      log.error("[queryOrder] response error, request={}, response={}", requestPMap, resContent);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    PMap responsePMap = alipayMap.getPMap("response").getPMap("trade");
    //验签
    result = verifySignMD5(responsePMap, md5securityKey, alipayMap.getString("sign"));
    if (!Result.isSuccess(result)) return result;

    //提取关键信息
    String trade_status = getTradeStatus(responsePMap.getString("trade_status"));
    return ResultMap.build().addItem("payStatus", trade_status);
  }

  private String getTradeStatus(String alipayTradeStatus) {
    if (alipayTradeStatus == null) return TRADE_STATUS.get("DEFAULT");
    String trade_status = TRADE_STATUS.get(alipayTradeStatus);
    if (trade_status == null) return TRADE_STATUS.get("DEFAULT");
    return trade_status;
  }

  @Override
  public ResultMap refundOrder(PMap params) throws ServiceException {
    ResultMap result;
    //组装参数
    PMap requestPMap = new PMap();
    requestPMap.put("service", ALIPAY_SERVICE_REFUND);                     //接口名
    requestPMap.put("partner", params.getString("merchantNo"));                    //商户号
    requestPMap.put("_input_charset", INPUT_CHARSET);                   //编码
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
      log.error("[refundOrder] request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    String md5securityKey = params.getString("md5securityKey");
    result = signMD5(requestPMap, md5securityKey);
    if (!Result.isSuccess(result)) return result;

    //发起请求
    Result httpResponse = HttpService.getInstance().doPost(params.getString("refundUrl"), requestPMap, INPUT_CHARSET, null);
    if (!Result.isSuccess(httpResponse)) {
      log.error("[refundOrder] http request failed, url={}, params={}", params.getString("refundUrl"), requestPMap);
      return ResultMap.build(ResultStatus.THIRD_HTTP_ERROR);
    }
    String resContent = (String) httpResponse.getReturnValue();
    //解析响应
    PMap alipayMap;
    try {
      alipayMap = XMLUtil.XML2PMap(resContent);
    } catch (Exception e) {
      log.error("[refundOrder] response error, request={}, response={}", requestPMap, resContent);
      throw new ServiceException(e, ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    //验证结果
    String is_success = alipayMap.getString("is_success");
    if (!"T".equals(is_success)) {
      log.error("[refundOrder] response error, request={}, response={}", requestPMap, resContent);
      result = ResultMap.build();
      result.addItem("errorCode", is_success);
      result.addItem("errorMsg", alipayMap.getString("error"));
      return (ResultMap) result.withError(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    return ResultMap.build().addItem("agencyRefundId", params.getString("refundSerialNumber"));
  }

  @Override
  public ResultMap queryRefundOrder(PMap params) throws ServiceException {
    ResultMap result;
    //组装参数
    PMap requestPMap = new PMap();
    requestPMap.put("service", ALIPAY_SERVICE_QUERY_REFUND);                  //查询订单接口名
    requestPMap.put("partner", params.getString("merchantNo"));                //商户号
    requestPMap.put("_input_charset", INPUT_CHARSET);               //编码
    requestPMap.put("batch_no", params.getString("refundSerialNumber"));             //退款号
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[queryRefundOrder] request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    String md5securityKey = params.getString("md5securityKey");
    result = signMD5(requestPMap, md5securityKey);
    if (!Result.isSuccess(result)) return result;

    //发起请求
    Result httpResponse = HttpService.getInstance().doPost(params.getString("queryRefundUrl"), requestPMap, INPUT_CHARSET, null);
    if (!Result.isSuccess(httpResponse)) {
      log.error("[queryRefundOrder] http request failed, url={}, params={}", params.getString("queryRefundUrl"), requestPMap);
      return ResultMap.build(ResultStatus.THIRD_HTTP_ERROR);
    }
    String resContent = (String) httpResponse.getReturnValue();
    //解析响应
    ResultMap refundResult;
    try {
      refundResult = HttpUtil.extractParams(resContent);
    } catch (Exception e) {
      log.error("[queryRefundOrder] response error, request={}, response={}", requestPMap, resContent);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    //验签
    result = verifySignMD5(refundResult.getData(), md5securityKey, (String) refundResult.getItem("sign"));
    if (!Result.isSuccess(result)) return result;
    //验证结果
    String is_success = (String) refundResult.getItem("is_success");
    if (!"T".equals(is_success)) {
      log.error("[queryRefundOrder] response error, request={}, response={}", requestPMap, refundResult);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    String result_details = (String) refundResult.getItem("result_details");
    if (StringUtil.isEmpty(result_details)) {
      log.error("[queryRefundOrder] response error, request={}, response={}", requestPMap, refundResult);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    String[] refundStrArr = result_details.split("\\^");
    String returnRefundId = refundStrArr[0];
    String returnRefundMon = refundStrArr[2];
    String refundIsSuccess = refundStrArr[3];
    if (StringUtil.isEmpty(returnRefundId, returnRefundMon, refundIsSuccess)) {
      log.error("[queryRefundOrder] response error, request={}, response={}", requestPMap, refundResult);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    if (!returnRefundId.equals(params.getString("refundSerialNumber"))) {
      log.error("[queryRefundOrder] response error, request={}, response={}", requestPMap, refundResult);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    return ResultMap.build().addItem("refundStatus", refundIsSuccess);
  }

  @Override
  public ResultMap downloadOrder(PMap params) throws ServiceException {
    ResultMap result;
    //组装参数
    PMap requestPMap = new PMap();
    requestPMap.put("service", ALIPAY_SERVICE_PAGE_QUERY);//接口名称
    requestPMap.put("partner", params.getString("merchantNo"));//商户号
    requestPMap.put("_input_charset", INPUT_CHARSET);//编码字符集
    requestPMap.put("page_no", params.getString("pageNo"));//查询页号
    requestPMap.put("pageSize", params.getString("pageSize"));
    // yyyyMMdd -> yyyy-MM-dd
    Date checkDate = (Date) params.get("checkDate");
    String alipayCheckDate = DateUtil.format(checkDate, DateUtil.DATE_FORMAT_DAY);
    String startTime = alipayCheckDate + " 00:00:00";
    String endTime = alipayCheckDate + " 23:59:59";
    requestPMap.put("gmt_start_time", startTime);//账务查询开始时间
    requestPMap.put("gmt_end_time", endTime);//账务查询结束时间
    CheckType checkType = (CheckType) params.get("checkType");
    if (checkType != CheckType.ALL) {
      log.error("[downloadOrder] request params error, params={}", params);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    requestPMap.put("trans_code", CHECK_TYPE.get(checkType));
    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[downloadOrder] request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }

    //签名
    String md5securityKey = params.getString("md5securityKey");
    result = signMD5(requestPMap, md5securityKey);
    if (!Result.isSuccess(result)) return result;

    //发起请求
    Result httpResponse = HttpService.getInstance().doPost(params.getString("downloadUrl"), requestPMap, INPUT_CHARSET, null);
    if (!Result.isSuccess(httpResponse)) {
      log.error("[downloadOrder] http request failed, url={}, params={}", params.getString("downloadUrl"), requestPMap);
      return ResultMap.build(ResultStatus.THIRD_HTTP_ERROR);
    }
    String resContent = (String) httpResponse.getReturnValue();
    //解析响应
    return validateAndParseMessage(resContent);
  }


  private ResultMap validateAndParseMessage(String message) {
    ResultMap result = ResultMap.build();
    SAXReader reader = new SAXReader();
    reader.setEncoding(INPUT_CHARSET);
    Document doc = null;
    try {
      doc = reader.read(new StringReader(message));
    } catch (Exception e) {
      log.error("[validateAndParseMessage] response error, message={}", message);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    String alipayIsSuccess = doc.selectSingleNode("/alipay/is_success").getText();
    //判断请求是否成功
    if (!"T".equals(alipayIsSuccess)) {
      String errorText = doc.selectSingleNode("/alipay/error").getText();
      log.error("[validateAndParseMessage] response error, is_success!=T, {}", errorText);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);

    }
    //判断是否还有下一页
    Node hasNextPage = doc.selectSingleNode("/alipay/response/account_page_query_result/has_next_page");
    result.addItem("hasNextPage", hasNextPage != null && "T".equals(hasNextPage.getText()));

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    List<Node> accountLogVoList;
    Node item;
    List<OutCheckRecord> list;
    HashMap<String, List<OutCheckRecord>> records = new HashMap<>();
    records.put("在线支付", new LinkedList<>());
    records.put("转账", new LinkedList<>());
    records.put("收费", new LinkedList<>());
    records.put("提现", new LinkedList<>());
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
        log.error("[validateAndParseMessage] response error, trans_date={}", item.getText());
      }
      //业务类型
      item = accountLogVo.selectSingleNode("trans_code_msg");
      list = records.get(item.getText());
      if (list == null)
        log.error("[validateAndParseMessage] response error, trans_code_msg={}", item.getText());
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
    ResultMap result;
    //组装参数
    PMap requestPMap = new PMap();
    requestPMap.put("service", ALIPAY_SERVICE_BATCH_TRANS);//接口名称
    requestPMap.put("partner", params.getString("merchantNo"));//商户号
    requestPMap.put("_input_charset", INPUT_CHARSET);//编码字符集
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
      log.error("[prepareTransferInfo] request params error, params={}", params);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    String md5securityKey = params.getString("md5securityKey");
    result = signMD5(requestPMap, md5securityKey);
    if (!Result.isSuccess(result)) return result;

    //生成支付URL
    String returnUrl = HttpUtil.packHttpGetUrl(params.getString("payUrl"), requestPMap);
    return ResultMap.build().addItem("returnUrl", returnUrl);
  }

  @Override
  public ResultMap queryTransfer(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap queryTransferRefund(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifyWebSync(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    String is_success = params.getString("is_success");//接口是否调用成功
    String out_trade_no = params.getString("out_trade_no");
    if (!"T".equals(is_success) || out_trade_no == null) {
      log.error("[getReqIDFromNotifyWebSync] out_trade_no not exists, params={}", params);
      result.withError(ResultStatus.THIRD_NOTIFY_PARAM_ERROR);
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
      log.error("[getReqIDFromNotifyWebAsync] out_trade_no not exists, params={}", params);
      result.withError(ResultStatus.THIRD_NOTIFY_PARAM_ERROR);
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
      log.error("[getReqIDFromNotifyRefund] batch_no not exists, params={}", params);
      result.withError(ResultStatus.THIRD_NOTIFY_PARAM_ERROR);
      return result;
    }
    result.addItem("reqId", batch_no);//商户网站唯一订单号
    return result;
  }

  public ResultMap getReqIDFromNotifyTransfer(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    String batch_no = params.getString("batch_no");
    if (batch_no == null) {
      log.error("[getReqIDFromNotifyTransfer] batch_no not exists, params={}", params);
      result.withError(ResultStatus.THIRD_NOTIFY_PARAM_ERROR);
      return result;
    }
//        String pay_user_id = params.getString("pay_user_id");
    result.addItem("reqId", batch_no);//商户网站唯一订单号
//        result.addItem("merchantNo", pay_user_id);//商户号
    return result;
  }

  public ResultMap handleNotifyWebSync(PMap params) throws ServiceException {
    ResultMap result;
    PMap notifyParams = params.getPMap("data");
    String md5securityKey = params.getString("md5securityKey");
    //验签
    result = verifySignMD5(notifyParams, md5securityKey, notifyParams.getString("sign"));
    if (!Result.isSuccess(result)) return result;

    //提取关键信息
    String out_trade_no = notifyParams.getString("out_trade_no");
    String trade_status = getTradeStatus(notifyParams.getString("trade_status"));

    result = ResultMap.build();
    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
    result.addItem("payStatus", trade_status);//交易状态

    return result;
  }

  public ResultMap handleNotifyWebAsync(PMap params) throws ServiceException {
    ResultMap result;
    PMap notifyParams = params.getPMap("data");
    String md5securityKey = params.getString("md5securityKey");
    //验签
    result = verifySignMD5(notifyParams, md5securityKey, notifyParams.getString("sign"));
    if (!Result.isSuccess(result)) return result;

    //提取关键信息
    String out_trade_no = notifyParams.getString("out_trade_no");
    String trade_no = notifyParams.getString("trade_no");
    String trade_status = getTradeStatus(notifyParams.getString("trade_status"));
    String out_channel_type = notifyParams.getString("out_channel_type");
    String gmt_payment = notifyParams.getString("gmt_payment");
    String total_fee = notifyParams.getString("total_fee");

    result = ResultMap.build();
    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
    result.addItem("agencyPayId", trade_no);//第三方订单号
    result.addItem("payStatus", trade_status);//交易状态
    result.addItem("agencyPayTime", gmt_payment);//第三方支付时间
    result.addItem("payMoney", total_fee);//支付金额
    result.addItem("channelType", out_channel_type);//第三方渠道方式

    return result;
  }

  public ResultMap handleNotifyWapSync(PMap params) throws ServiceException {
    return handleNotifyWebSync(params);
  }

  public ResultMap handleNotifyWapAsync(PMap params) throws ServiceException {
    return handleNotifyWebAsync(params);
  }

  public ResultMap handleNotifySDKAsync(PMap params) throws ServiceException {
    ResultMap result;
    PMap notifyParams = params.getPMap("data");
    //验签
    String publicCertFilePath = params.getString("publicCertFilePath");
    String publicCertKey = SecretKeyUtil.loadKeyFromFile(publicCertFilePath);
    if (StringUtil.isEmpty(publicCertKey)) {
      log.error("[handleNotifySDKAsync] get public key failed, params={}", publicCertFilePath);
      return ResultMap.build(ResultStatus.THIRD_GET_KEY_ERROR);
    }
    String out_sign = notifyParams.getString("sign");
    if (!SecretKeyUtil.aliRSACheckSign(notifyParams, out_sign, publicCertKey)) {
      log.error("[handleNotifySDKAsync] verify sign failed, params={}, sign={}", params, out_sign);
      return ResultMap.build(ResultStatus.THIRD_VERIFY_SIGN_ERROR);
    }

    //提取关键信息
    String out_trade_no = notifyParams.getString("out_trade_no");
    String trade_no = notifyParams.getString("trade_no");
    String trade_status = getTradeStatus(notifyParams.getString("trade_status"));
    String gmt_payment = notifyParams.getString("gmt_payment");
    String total_fee = notifyParams.getString("total_fee");

    result = ResultMap.build();
    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
    result.addItem("agencyPayId", trade_no);//第三方订单号
    result.addItem("payStatus", trade_status);//交易状态
    result.addItem("agencyPayTime", gmt_payment);//第三方支付时间
    result.addItem("payMoney", total_fee);//支付金额

    return result;
  }

  public ResultMap handleNotifyRefund(PMap params) throws ServiceException {
    ResultMap result;
    PMap notifyParams = params.getPMap("data");
    String md5securityKey = params.getString("md5securityKey");
    //验签
    result = verifySignMD5(notifyParams, md5securityKey, notifyParams.getString("sign"));
    if (!Result.isSuccess(result)) return result;

    //提取关键信息
    String batch_no = notifyParams.getString("batch_no");
    String result_details = notifyParams.getString("result_details");
    String[] refund_details = result_details.split("#");
    if (refund_details.length != 1) {
      log.error("[handleNotifyRefund] response error, params={}", notifyParams);
      return ResultMap.build(ResultStatus.THIRD_NOTIFY_PARAM_ERROR);

    }
    String refund_detail = refund_details[0].split("\\$")[0];
    String[] refund_detail_items = refund_detail.split("\\^");
    if (refund_detail_items.length != 3) {
      log.error("[handleNotifyRefund] response error, params={}", notifyParams);
      return ResultMap.build(ResultStatus.THIRD_NOTIFY_PARAM_ERROR);

    }
    String refund_money = refund_detail_items[1];
    String refund_status = refund_detail_items[2];

    result = ResultMap.build();
    result.addItem("reqId", batch_no);//商户网站唯一退款单号
    result.addItem("agencyRefundId", batch_no);//第三方退款单号
    result.addItem("refundStatus", refund_status);//退款状态
    result.addItem("refundMoney", refund_money);//退款金额

    return result;
  }

  public ResultMap handleNotifyTransfer(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  private ResultMap signMD5(PMap requestPMap, String secretKey) {
    String sign =
            SecretKeyUtil.aliMD5Sign(requestPMap, secretKey);
    if (sign == null) {
      log.error("[signMD5] sign failed, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_SIGN_ERROR);
    }
    requestPMap.put("sign", sign);//签名
    requestPMap.put("sign_type", SIGN_TYPE);//签名方式
    return ResultMap.build();
  }

  private ResultMap verifySignMD5(PMap responsePMap, String secretKey, String sign) {
    boolean signOK = SecretKeyUtil
            .aliMD5CheckSign(responsePMap, secretKey, sign);
    if (!signOK) {
      log.error("[verifySignMD5] verify sign failed, responsePMap={}, sign={}",
              responsePMap, sign);
      return ResultMap.build(ResultStatus.THIRD_VERIFY_SIGN_ERROR);
    }
    return ResultMap.build();
  }
}
