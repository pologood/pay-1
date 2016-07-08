package com.sogou.pay.thirdpay.service.Wechat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sogou.pay.common.model.StdPayRequest;
import com.sogou.pay.common.enums.OrderRefundStatus;
import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.HttpService;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.common.utils.XMLUtil;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.model.OutCheckRecord;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.service.ThirdpayService;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayUtils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by xiepeidong on 2016/1/27.
 */
@Service
public class WechatService implements ThirdpayService {

  /*微信支付参数*/
  public static final String FEE_TYPE = "CNY"; //币种

  public static final String INPUT_CHARSET = "UTF-8"; // 字符编码格式

  public static final String QR_TRADE_TYPE = "NATIVE";//扫码交易类型

  public static final String SDK_TRADE_TYPE = "APP";//SDK交易类型

  private static final Logger LOGGER = LoggerFactory.getLogger(WechatService.class);

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

  private ResultMap<?> doRequest(String url, String md5Key, PMap<String, Object> requestPMap) throws ServiceException {
    if (!MapUtil.checkAllExist(requestPMap)) {
      LOGGER.error("[doRequest]request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    ResultMap<?> result = signMD5(requestPMap, md5Key);
    if (!Result.isSuccess(result)) return result;

    //发起请求
    String paramsStr = XMLUtil.Map2XML("xml", requestPMap);
    Result<?> httpResponse = HttpService.getInstance().doPost(url, paramsStr, INPUT_CHARSET, null);
    if (!Result.isSuccess(httpResponse)) {
      LOGGER.error("[prepay]http request failed, url={}, params={}", url, paramsStr);
      return ResultMap.build(ResultStatus.THIRD_HTTP_ERROR);
    }
    String resContent = (String) httpResponse.getReturnValue();

    //解析响应
    PMap<String, ?> responsePMap = null;
    try {
      responsePMap = XMLUtil.XML2PMap(resContent);
    } catch (Exception e) {
      LOGGER.error("[prepay]response error, request={}, response={}", requestPMap, resContent);
      throw new ServiceException(e, ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }

    //检查返回参数
    if (StringUtils.isEmpty(responsePMap.getString("return_code"))
            || StringUtils.isEmpty(responsePMap.getString("result_code"))
            || StringUtils.isEmpty(responsePMap.getString("sign"))) {
      LOGGER.error("[prepay] response error, request={}, response={}", requestPMap, responsePMap);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }

    //验签
    result = verifySignMD5(responsePMap, md5Key, responsePMap.getString("sign"));
    if (!Result.isSuccess(result)) return result;

    return result.addItems(responsePMap);
  }

  private ResultMap<?> prepay(StdPayRequest params, String trade_type) throws ServiceException {
    //组装参数
    PMap<String, Object> requestPMap = new PMap<>();
    requestPMap.put("appid", params.getPayee()); // 公众账号ID
    requestPMap.put("mch_id", params.getMerchantId()); // 商户号
    requestPMap.put("nonce_str", TenpayUtils.getNonceStr()); // 随机字符串，不长于32位
    requestPMap.put("body", params.getProductName()); // 商品描述
    requestPMap.put("out_trade_no", params.getPayId()); //订单号
    requestPMap.put("fee_type", FEE_TYPE); //支付币种
    String orderAmount = TenpayUtils.fenParseFromYuan(params.getOrderAmount());
    requestPMap.put("total_fee", orderAmount); //总金额
    requestPMap.put("spbill_create_ip", "127.0.0.1"); //买家IP
    requestPMap.put("notify_url", params.getServerNotifyUrl()); //异步回调地址
    requestPMap.put("trade_type", trade_type); //交易类型
    if (trade_type.equals(QR_TRADE_TYPE)) requestPMap.put("product_id", params.getPayId());

    ResultMap<?> result = doRequest(params.getPrepayUrl(), params.getMd5Key(), requestPMap);
    if (!Result.isSuccess(result)) {
      LOGGER.error("[prepay] failed, params={}", params);
      return result;
    }

    PMap<String, Object> responsePMap = result.getData();

    String return_trade_type = responsePMap.getString("trade_type");
    if (!return_trade_type.equals(trade_type)) {
      LOGGER.error("[prepay] response error, request={}, response={}", requestPMap, responsePMap);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    return ResultMap.build().addItems(responsePMap);
  }

  @Override
  public ResultMap<?> preparePayInfoQRCode(StdPayRequest params) throws ServiceException {
    ResultMap<?> result = prepay(params, QR_TRADE_TYPE);
    if (!Result.isSuccess(result)) return result;

    PMap<String, ?> prepayPMap = result.getData();
    //返回二维码图片数据
    return ResultMap.build().addItem("qrCode", text2QRCode(prepayPMap.getString("code_url")));
  }

  @Override
  public ResultMap<?> preparePayInfoSDK(StdPayRequest params) throws ServiceException {
    ResultMap<?> result = prepay(params, SDK_TRADE_TYPE);
    if (!Result.isSuccess(result)) return result;

    PMap<String, ?> prepayPMap = result.getData();

    //组装参数
    PMap<String, Object> requestPMap = new PMap<>();
    requestPMap.put("appid", params.getPayee());
    requestPMap.put("partnerid", params.getMerchantId());
    requestPMap.put("prepayid", prepayPMap.get("prepay_id"));
    requestPMap.put("package", "Sign=WXPay");
    requestPMap.put("noncestr", TenpayUtils.getNonceStr());
    requestPMap.put("timestamp", TenpayUtils.getTimeStamp());
    if (!MapUtil.checkAllExist(requestPMap)) {
      LOGGER.error("[preparePayInfoSDK] request params error, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    //签名
    result = signMD5(requestPMap, params.getMd5Key());
    if (!Result.isSuccess(result)) return result;

    return ResultMap.build().
            addItem("orderInfo", requestPMap).
            addItem("agencyCode", params.getAgencyCode());
  }

  /**
   * 微信查询订单信息 只能查询半年内的订单, 超过半年的订单调用此查询接口会报“88221009交易单不存在”
   */
  @Override
  public ResultMap<?> queryOrder(PMap<String, ?> params) throws ServiceException {
    //组装参数
    PMap<String, Object> requestPMap = new PMap<>();
    requestPMap.put("appid", params.getString("sellerEmail")); // 公众账号ID
    requestPMap.put("mch_id", params.getString("merchantNo")); // 商户号
    requestPMap.put("nonce_str", TenpayUtils.getNonceStr()); // 随机字符串，不长于32位
    requestPMap.put("out_trade_no", params.getString("serialNumber")); //商户订单号

    ResultMap<?> result = doRequest(params.getString("queryUrl"), params.getString("md5securityKey"), requestPMap);
    if (!Result.isSuccess(result)) {
      LOGGER.error("[queryOrder] failed, params={}", params);
      return result;
    }

    PMap<String, ?> responsePMap = result.getData();

    //返回交易状态
    return ResultMap.build().addItem("payStatus", getTradeStatus(responsePMap.getString("trade_state").toUpperCase()));
  }

  private String getTradeStatus(String wechatTradeStatus) {
    if (wechatTradeStatus == null) return TRADE_STATUS.get("DEFAULT");
    String trade_status = TRADE_STATUS.get(wechatTradeStatus);
    if (trade_status == null) return TRADE_STATUS.get("DEFAULT");
    return trade_status;
  }

  private String getRefundStatus(String wechatRefundStatus) {
    if (wechatRefundStatus == null) return REFUND_STATUS.get("DEFAULT");
    String refund_status = REFUND_STATUS.get(wechatRefundStatus);
    if (refund_status == null) return REFUND_STATUS.get("DEFAULT");
    return refund_status;
  }

  /**
   * 微信订单退款 只能退半年内的订单, 超过半年的订单调用此退款接口会报“88221009交易单不存在”
   */
  @Override
  public ResultMap<?> refundOrder(PMap<String, ?> params) throws ServiceException {

    //组装参数
    PMap<String, Object> requestPMap = new PMap<>();
    requestPMap.put("appid", params.getString("sellerEmail")); // 公众账号ID
    requestPMap.put("mch_id", params.getString("merchantNo")); // 商户号
    requestPMap.put("nonce_str", TenpayUtils.getNonceStr()); // 随机字符串，不长于32位
    requestPMap.put("transaction_id", params.getString("agencySerialNumber")); //微信订单号
    requestPMap.put("out_trade_no", params.getString("serialNumber")); //订单号
    requestPMap.put("out_refund_no", params.getString("refundSerialNumber")); //商户退款号
    String total_fee = TenpayUtils.fenParseFromYuan(params.getString("totalAmount"));
    requestPMap.put("total_fee", total_fee); //总金额
    String refundAmount = TenpayUtils.fenParseFromYuan(params.getString("refundAmount"));
    requestPMap.put("refund_fee", refundAmount); //退款金额
    requestPMap.put("refund_fee_type", FEE_TYPE); //货币种类
    requestPMap.put("op_user_id", params.getString("merchantNo")); //操作员

    ResultMap<?> result = doRequest(params.getString("refundUrl"), params.getString("md5securityKey"), requestPMap);
    if (!Result.isSuccess(result)) {
      LOGGER.error("[refundOrder] failed, params={}", params);
      return result;
    }

    PMap<String, ?> responsePMap = result.getData();

    result = ResultMap.build().addItem("agencyRefundId", responsePMap.getString("refund_id"));

    String result_code = responsePMap.getString("result_code");
    if (!"SUCCESS".equals(result_code)) {
      LOGGER.error("[refundOrder] response error, request={}, response={}", requestPMap, responsePMap);
      result.withError(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
      result.addItem("errorCode", responsePMap.getString("err_code"));
      result.addItem("errorMsg", responsePMap.getString("err_code_des"));
    }

    //返回退款结果
    return result;
  }

  /**
   * 微信查询订单退款信息 只能查询半年内的订单, 超过半年的订单调用此查询接口会报“88221009交易单不存在”
   */
  @Override
  public ResultMap<?> queryRefundOrder(PMap<String, ?> params) throws ServiceException {
    //组装参数
    PMap<String, Object> requestPMap = new PMap<>();
    requestPMap.put("appid", params.getString("sellerEmail")); // 公众账号ID
    requestPMap.put("mch_id", params.getString("merchantNo")); // 商户号
    requestPMap.put("nonce_str", TenpayUtils.getNonceStr()); // 随机字符串，不长于32位
    requestPMap.put("out_refund_no", params.getString("refundSerialNumber")); //商户退款号

    ResultMap<?> result = doRequest(params.getString("queryRefundUrl"), params.getString("md5securityKey"),
            requestPMap);
    if (!Result.isSuccess(result)) {
      LOGGER.error("[queryRefundOrder] failed, params={}", params);
      return result;
    }

    PMap<String, ?> responsePMap = result.getData();

    //返回交易状态
    ResultMap.build().addItem("refundStatus", getRefundStatus(responsePMap.getString("refund_status_0").toUpperCase()));
    return result;
  }

  @Override
  public ResultMap<?> downloadOrder(PMap<String, ?> params) throws ServiceException {
    //组装参数
    Date checkDate = (Date) params.get("checkDate");
    String wechatCheckDate = DateUtil.format(checkDate, DateUtil.DATE_FORMAT_DAY_SHORT);

    PMap<String, Object> requestPMap = new PMap<>();
    requestPMap.put("appid", params.getString("sellerEmail"));//公众账号ID
    requestPMap.put("mch_id", params.getString("merchantNo"));//商户号
    requestPMap.put("nonce_str", TenpayUtils.getNonceStr()); //随机32位字符串
    requestPMap.put("bill_date", wechatCheckDate);//对账单日起
    if (!MapUtil.checkAllExist(requestPMap)) {
      LOGGER.error("[downloadOrder] request params error, params={}", params);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
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
      LOGGER.error("[downloadOrder] request params error, params={}", params);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }

    //签名
    String md5securityKey = params.getString("md5securityKey");
    ResultMap<?> result = signMD5(requestPMap, md5securityKey);
    if (!Result.isSuccess(result)) return result;

    //发起请求
    String paramsStr = XMLUtil.Map2XML("xml", requestPMap);
    Result<?> httpResponse = HttpService.getInstance().doPost(params.getString("downloadUrl"), paramsStr, INPUT_CHARSET,
            null);
    if (!Result.isSuccess(httpResponse)) {
      LOGGER.error("[downloadOrder] http request failed, url={}, params={}", params.getString("downloadUrl"), paramsStr);
      return ResultMap.build(ResultStatus.THIRD_HTTP_ERROR);
    }

    String resContent = (String) httpResponse.getReturnValue();
    //解析响应
    return validateAndParseMessage(resContent);
  }

  private ResultMap<?> validateAndParseMessage(String message) {
    ResultMap<?> result = ResultMap.build();
    String line = null;
    BufferedReader reader = null;
    List<OutCheckRecord> payRecords = new LinkedList<>();
    List<OutCheckRecord> refRecords = new LinkedList<>();
    System.out.println(message);

    try {
      result.addItem("hasNextPage", false);

      if (message.startsWith("<xml>")) {
        PMap<String, ?> pMap = XMLUtil.XML2PMap(message);
        String errorText = String.valueOf(pMap.get("return_msg"));
        LOGGER.error("[validateAndParseMessage] response error, message={}", message);
        //没有对账单数据
        if ("No Bill Exist".equals(errorText)) {
          return result;
        }
        result.withError(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
        return result;
      }

      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      reader = new BufferedReader(new StringReader(message));
      line = reader.readLine();// 第一行是表头，忽略
      if (line == null) {
        LOGGER.error("[validateAndParseMessage] response error, message={}", message);
        result.withError(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
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
      reader.close();
      result.addItem("payRecords", payRecords);
      result.addItem("refRecords", refRecords);
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("[validateAndParseMessage] response error, message={}", message);
      result.withError(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    return result;
  }

  //实际上，微信扫码支付并没有同步通知，是由搜狗支付中心页面模拟发起的同步通知
  public ResultMap<?> getReqIDFromNotifyWebSync(PMap<String, ?> params) throws ServiceException {
    ResultMap<?> result = ResultMap.build();
    String out_trade_no = params.getString("out_trade_no");
    if (out_trade_no == null) {
      LOGGER.error("[getReqIDFromNotifyWebSync] out_trade_no not exists, params={}", params);
      result.withError(ResultStatus.THIRD_NOTIFY_PARAM_ERROR);
      return result;
    }
    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
    return result;
  }

  public ResultMap<?> getReqIDFromNotifyWebAsync(PMap<String, ?> params) throws ServiceException {
    ResultMap<?> result = ResultMap.build();
    String return_code = params.getString("return_code");
    String out_trade_no = params.getString("out_trade_no");
    if (!return_code.equals("SUCCESS") || StringUtils.isEmpty(out_trade_no)) {
      LOGGER.error("[getReqIDFromNotifyWebAsync] out_trade_no not exists, params={}", params);
      result.withError(ResultStatus.THIRD_NOTIFY_PARAM_ERROR);
      return result;
    }
    //        String mch_id = params.getString("mch_id");
    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
    //        result.addItem("merchantNo", mch_id);//商户号
    return result;
  }

  //实际上，微信扫码支付并没有同步通知，是由搜狗支付中心页面模拟发起的同步通知
  public ResultMap<?> handleNotifyWebSync(PMap<String, ?> params) throws ServiceException {
    ResultMap<?> result = ResultMap.build();
    PMap<String, ?> notifyParams = (PMap<String, ?>) params.getPMap("data");
    //校验签名
    //        String md5securityKey = params.getString("md5securityKey");
    //        String out_sign = notifyParams.getString("sign");
    //        if (!SecretKeyUtil.tenMD5CheckSign(notifyParams, md5securityKey, out_sign, INPUT_CHARSET)) {
    //            result.withError(ResultStatus.THIRD_NOTIFY_SYNC_SIGN_ERROR);
    //            return result;
    //        }
    //提取关键参数
    String out_trade_no = notifyParams.getString("out_trade_no");
    String result_code = getTradeStatus(notifyParams.getString("result_code"));

    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
    result.addItem("payStatus", result_code);//交易状态

    return result;
  }

  public ResultMap<?> handleNotifyWebAsync(PMap<String, ?> params) throws ServiceException {
    ResultMap<?> result;
    PMap<String, ?> notifyParams = (PMap<String, ?>) params.getPMap("data");
    String md5securityKey = params.getString("md5securityKey");
    //验签
    result = verifySignMD5(notifyParams, md5securityKey, notifyParams.getString("sign"));
    if (!Result.isSuccess(result)) return result;

    //提取关键信息
    String out_trade_no = notifyParams.getString("out_trade_no");
    String transaction_id = notifyParams.getString("transaction_id");
    String result_code = getTradeStatus(notifyParams.getString("result_code"));
    String time_end = notifyParams.getString("time_end");
    String total_fee = notifyParams.getString("total_fee");
    total_fee = String.valueOf(new BigDecimal(total_fee).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UP));

    result = ResultMap.build();
    result.addItem("reqId", out_trade_no);//商户网站唯一订单号
    result.addItem("agencyPayId", transaction_id);//第三方订单号
    result.addItem("payStatus", result_code);//交易状态
    result.addItem("agencyPayTime", time_end);//第三方支付时间
    result.addItem("payMoney", total_fee);//支付金额

    return result;
  }

  public ResultMap<?> handleNotifySDKAsync(PMap<String, ?> params) throws ServiceException {
    return handleNotifyWebAsync(params);
  }

  private String text2QRCode(String content) throws ServiceException {
    try {
      Map<EncodeHintType, String> hints = new HashMap<>();
      hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
      BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
      BufferedImage qrcodeImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(qrcodeImg, "PNG", baos);
      baos.close();
      String base64 = org.apache.commons.codec.binary.Base64.encodeBase64String(baos.toByteArray());
      base64 = URLEncoder.encode(base64, "UTF-8");
      return String.format("data:image/png;base64,%s", base64);
    } catch (Exception e) {
      LOGGER.error("[text2QRCode] failed, params={}, {}", content, e.getStackTrace());
      throw new ServiceException(ResultStatus.THIRD_ERROR);
    }
  }

  private ResultMap<?> signMD5(PMap<String, Object> requestPMap, String secretKey) {
    String sign = SecretKeyUtil.tenMD5Sign(requestPMap, secretKey);
    if (sign == null) {
      LOGGER.error("[signMD5] sign failed, params={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_SIGN_ERROR);
    }
    requestPMap.put("sign", sign);//签名
    return ResultMap.build();
  }

  private ResultMap<?> verifySignMD5(PMap<String, ?> responsePMap, String secretKey, String sign) {
    if (!SecretKeyUtil.tenMD5CheckSign(responsePMap, secretKey, sign)) {
      LOGGER.error("[verifySignMD5]failed responsePMap={}, sign={}", responsePMap, sign);
      return ResultMap.build(ResultStatus.THIRD_VERIFY_SIGN_ERROR);
    }
    return ResultMap.build();
  }
}
