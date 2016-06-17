/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.thirdpay.service.Unionpay;

import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ReturnRowsClause;
import com.google.common.collect.ImmutableMap;
import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.client.HttpService;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.thirdpay.biz.enums.InternalChannelType;
import com.sogou.pay.thirdpay.biz.enums.UnionpayBizType;
import com.sogou.pay.thirdpay.biz.enums.UnionpaySubTxnType;
import com.sogou.pay.thirdpay.biz.enums.UnionpayTxnType;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayUtils;
import com.sogou.pay.thirdpay.service.ThirdpayService;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年6月15日;
//-------------------------------------------------------
public class UnionpayService implements ThirdpayService {

  private static HashMap<String, String> TRADE_STATUS = new HashMap<String, String>();

  static {
    TRADE_STATUS.put("00", OrderStatus.SUCCESS.name());//交易成功结束
    TRADE_STATUS.put("A6", OrderStatus.SUCCESS.name());//交易有缺陷成功
    TRADE_STATUS.put("DEFAULT", OrderStatus.FAILURE.name());//默认
  }

  @Override
  public ResultMap preparePayInfoAccount(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap preparePayInfoGatway(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap preparePayInfoQRCode(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public ResultMap preparePayInfoSDK(PMap params) throws ServiceException {
    PMap requestMap = getPrepayReq(params, InternalChannelType.SDK);
    if (!MapUtil.checkAllExist(requestMap)) {
      LOG.error("[preparePayInfoSDK]empty param:{}", requestMap);
      return ResultMap.build(ResultStatus.PAY_PARAM_ERROR);
    }
    ResultMap signResult = sign(params, requestMap);
    if (!Result.isSuccess(signResult)) return signResult;
    ResultMap sendResult = send(params, requestMap);
    if (!Result.isSuccess(sendResult)) return sendResult;
    ResultMap signCheckResult = checkSign(params, sendResult.getData());
    if (!ResultMap.isSuccess(signCheckResult)) return signCheckResult;
    ResultMap resultMap = ResultMap.build();
    if (SUCCESS_RESPONSE_CODE != sendResult.getData().getString("respCode")) {
      resultMap.addItem("error_code", sendResult.getData().getString("respCode"));
      resultMap.addItem("error_msg", sendResult.getData().getString("respMsg"));
      resultMap.withError(ResultStatus.THIRD_PAY_ERROR);
    } else resultMap.addItem("orderInfo", sendResult.getData());
    return sendResult;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private ResultMap checkSign(PMap params, PMap signMap) {
    String publicCertKey = getKey(params, false);
    if (publicCertKey.length() == 0) {
      LOG.error("[checkSign]get public key error:{}", params);
      return ResultMap.build(ResultStatus.THIRD_PAY_GET_KEY_ERROR);
    }
    if (!SecretKeyUtil.unionRSACheckSign(signMap, (String) signMap.remove("signature"), publicCertKey, CHARSET)) {
      LOG.error("[checkSign]failed:{}", signMap);
      return ResultMap.build(ResultStatus.THIRD_PAY_RESPONSE_SIGN_ERROR);
    }
    return ResultMap.build();
  }
  
  @SuppressWarnings("rawtypes")
  private String getKey(PMap params,boolean isPrivate){
    String certFilePath = getCertFilePath(params, isPrivate);
    return SecretKeyUtil.loadKeyFromFile(certFilePath);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private ResultMap send(PMap params, PMap requestMap) {
    Result response = HttpService.getInstance().doPost(params.getString("payUrl"), requestMap, CHARSET, null);
    if (!Result.isSuccess(response)) {
      LOG.error("[send]http request error:{}", requestMap);
      return ResultMap.build(ResultStatus.THIRD_PAY_HTTP_ERROR);
    }
    String resContent = (String) response.getReturnValue();
    ResultMap responseMap;
    if (StringUtils.isBlank(resContent) || !ResultMap.isSuccess(responseMap = HttpUtil.extractUrlParams(resContent))
        || MapUtils.isEmpty(responseMap.getData())) {
      LOG.error("[send]http response error:params={} and respnose={}", requestMap, resContent);
      return ResultMap.build(ResultStatus.THIRD_PAY_RESPONSE_PARAM_ERROR);
    }
    return responseMap;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private ResultMap sign(PMap params, PMap signMap) {
    String privateCertKey = getKey(params, true);
    if (privateCertKey.length() == 0) {
      LOG.error("[sign]get private key error:{}", params);
      return ResultMap.build(ResultStatus.THIRD_PAY_GET_KEY_ERROR);
    }
    String sign = SecretKeyUtil.unionRSASign(signMap, privateCertKey, CHARSET);
    if (sign == null) {
      LOG.error("[sign]error:{}", signMap);
      return ResultMap.build(ResultStatus.THIRD_PAY_SIGN_ERROR);
    }
    signMap.put("signature", sign);
    return ResultMap.build();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private PMap getPrepayReq(PMap params, InternalChannelType internalChannelType) {
    PMap result = new PMap<>();

    /*必填*/
    result.put("version", VERSION);//版本号 
    result.put("encoding", CHARSET);//编码方式 
    result.put("certId", CERTID);//证书ID 
    result.put("signMethod", SIGNMETHOD);//签名方法 
    result.put("txnType", UnionpayTxnType.CONSUMPTION.getValue());//交易类型 
    result.put("txnSubType", UnionpaySubTxnType.SELF_SERVICE_CONSUMPTION.getValue());//交易子类 
    result.put("bizType", UnionpayBizType.B2C_GATEWAY_PAYMENT.getValue());//产品类型 
    result.put("channelType", channelMap.get(internalChannelType));//渠道类型 
    result.put("backUrl", params.getString("serverNotifyUrl"));//后台通知地址 
    result.put("accessType", ACCESSTYPE);//接入类型 
    result.put("merId", params.getString("merchantNo"));//商户代码 
    result.put("orderId", params.getString("serialNumber"));//商户订单号 
    result.put("txnTime", LocalDateTime.now().format(FORMATTER));//订单发送时间 
    result.put("txnAmt", TenpayUtils.fenParseFromYuan(params.getString("orderAmount")));//交易金额 
    result.put("currencyCode", CURRENCYCODE);//交易币种 

    /*选填*/
    if (StringUtils.isNotBlank(params.getString("accountId"))) result.put("accNo", params.getString("accountId"));//账号 1后台类消费交易时上送全卡号或卡号后 4位;2跨行收单且收单机构收集银行卡信息时上送;3前台类交易可通过配置后返回,卡号可选上送
    if (ChannelType.MOBILE.getValue().equalsIgnoreCase(result.getString("channelType")))
      result.put("orderDesc", params.getString("subject"));//订单描述 移动支付上送

    return result;
  }

  @Override
  public ResultMap preparePayInfoWap(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private String getCertFilePath(PMap params, boolean isPrivate) {
    return "e:" + params.getString(String.format("%sCertFilePath", isPrivate ? "private" : "public"));
  }

  private ResultMap doRequest(PMap params, PMap requestPMap) throws ServiceException {

    ResultMap result = ResultMap.build();

    if (!MapUtil.checkAllExist(requestPMap)) {
      LOG.error("[doRequest] 银联请求参数错误, 参数: {}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_REFUND_PARAM_ERROR);
    }
    //获取商户私钥路径
    String privateCertFilePath = "e:" + params.getString("privateCertFilePath");
    //获取商户私钥
    String privateCertKey = SecretKeyUtil.loadKeyFromFile(privateCertFilePath);
    if (privateCertKey.equals("")) {
      LOG.error("[doRequest] 银联请求获取第三方支付账户密钥失败, 参数: {}", privateCertFilePath);
      return ResultMap.build(ResultStatus.THIRD_PAY_GET_KEY_ERROR);
    }
    //签名
    String sign = SecretKeyUtil.unionRSASign(requestPMap, privateCertKey, CHARSET);
    if (sign == null) {
      LOG.error("[doRequest] 银联请求签名失败, 参数: {}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PAY_SIGN_ERROR);
    }
    requestPMap.put("signature", sign);

    Result httpResponse = HttpService.getInstance().doPost(params.getString("refundUrl"), requestPMap, CHARSET, null);
    if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
      LOG.error("[doRequest] 银联请求HTTP请求失败, 参数: {}", requestPMap);
      result.withError(ResultStatus.THIRD_REFUND_HTTP_ERROR);
      return result;
    }
    String resContent = (String) httpResponse.getReturnValue();
    //解析响应
    if (resContent == null) {
      LOG.error("[doRequest] 银联请求返回参数异常, 参数: {}", requestPMap);
      result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
      return result;
    }
    PMap resultPMap = HttpUtil.extractUrlParams(resContent).getData();
    //获取商户私钥路径
    String publicCertFilePath = "e:" + params.getString("publicCertFilePath");
    //获取银联公钥
    String publicCertKey = SecretKeyUtil.loadKeyFromFile(publicCertFilePath);
    if (publicCertKey.equals("")) {
      LOG.error("[doRequest] 银联请求获取银联公钥失败, 参数: {}", publicCertFilePath);
      return ResultMap.build(ResultStatus.THIRD_PAY_GET_KEY_ERROR);
    }
    //验证响应合法性
    sign = (String) resultPMap.remove("signature");
    boolean signOK = SecretKeyUtil.unionRSACheckSign(resultPMap, sign, publicCertKey, CHARSET);
    if (!signOK) {
      LOG.error("[doRequest] 银联请求返回参数签名错误, 参数: {}, 返回: {}", requestPMap, resultPMap);
      result.withError(ResultStatus.THIRD_REFUND_RESPONSE_SIGN_ERROR);
      return result;
    }
    String respCode = resultPMap.getString("respCode");
    if (!respCode.equals("00") && !respCode.equals("A6")) {
      LOG.error("[doRequest] 银联请求返回参数异常, respCode!=00/A6，参数: {}, 返回: {}", requestPMap, resultPMap);
      result.addItem("error_code", respCode);
      result.addItem("error_msg", resultPMap.getString("respMsg"));
      result.withError(ResultStatus.THIRD_REFUND_RESPONSE_PARAM_ERROR);
      return result;
    }
    return result.addItem("resultPMap", resultPMap);
  }

  private ResultMap query(PMap params, String resultName) throws ServiceException {
    PMap requestPMap = new PMap();
    //组装请求参数
    requestPMap.put("version", VERSION); //版本号
    requestPMap.put("encoding", CHARSET); //字符集编码
    requestPMap.put("signMethod", SIGNMETHOD); //签名方法 目前只支持01-RSA方式证书加密
    requestPMap.put("txnType", UnionpayTxnType.TRADE_INQUIRY.getValue()); //交易类型 00-查询
    requestPMap.put("txnSubType", UnionpaySubTxnType.DEFAULT.getValue()); //交易子类型  默认00
    requestPMap.put("bizType", UnionpayBizType.B2C_GATEWAY_PAYMENT.getValue()); //业务类型
    requestPMap.put("accessType", ACCESSTYPE); //接入类型，商户接入固定填0，不需修改
    requestPMap.put("merId", params.getString("merchantNo")); //商户号
    requestPMap.put("queryId", params.getString("serialNumber")); //原消费交易返回的的queryId

    ResultMap result = doRequest(params, requestPMap);
    if (!Result.isSuccess(result)) {
      LOG.error("[query] failed, params={}", params);
      return result;
    }

    PMap resultPMap = (PMap) result.getItem("resultPMap");
    return result.addItem(resultName, getTradeStatus(resultPMap.getString("origRespCode")));
  }

  @Override
  public ResultMap queryOrder(PMap params) throws ServiceException {
    return query(params, "order_state");
  }

  @Override
  public ResultMap refundOrder(PMap params) throws ServiceException {
    PMap requestPMap = new PMap();
    //组装请求参数
    requestPMap.put("version", VERSION); //版本号
    requestPMap.put("encoding", CHARSET); //字符集编码
    requestPMap.put("signMethod", SIGNMETHOD); //签名方法 目前只支持01-RSA方式证书加密
    requestPMap.put("txnType", UnionpayTxnType.REFUND.getValue()); //交易类型 04-退货
    requestPMap.put("txnSubType", UnionpaySubTxnType.DEFAULT.getValue()); //交易子类型  默认00
    requestPMap.put("bizType", UnionpayBizType.B2C_GATEWAY_PAYMENT.getValue()); //业务类型
    requestPMap.put("channelType", ChannelType.MOBILE.getValue()); //渠道类型，07-PC，08-手机
    requestPMap.put("merId", params.getString("merchantNo")); //商户号
    requestPMap.put("accessType", ACCESSTYPE); //接入类型，商户接入固定填0，不需修改
    requestPMap.put("orderId", params.getString("refundSerialNumber")); //商户退款单号，8-40位数字字母，不能含“-”或“_”
    requestPMap.put("txnTime", DateUtil.format(params.getDate("refundReqTime"), DateUtil.DATE_FORMAT_SECOND_SHORT)); //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
    requestPMap.put("currencyCode", CURRENCYCODE); //交易币种（境内商户一般是156 人民币）
    String refundAmount = TenpayUtils.fenParseFromYuan(params.getString("refundAmount"));
    requestPMap.put("txnAmt", refundAmount); //退货金额，单位分
    requestPMap.put("backUrl", params.getString("refundNotifyUrl")); //后台通知地址
    requestPMap.put("origQryId", params.getString("serialNumber")); //原消费交易返回的的queryId

    ResultMap result = doRequest(params, requestPMap);
    if (!Result.isSuccess(result)) {
      LOG.error("[refundOrder] failed, params={}", params);
      return result;
    }

    PMap resultPMap = (PMap) result.getItem("resultPMap");
    return result.addItem("third_refund_id", resultPMap.getString("queryId"));
  }

  @Override
  public ResultMap queryRefundOrder(PMap params) throws ServiceException {
    return query(params, "refund_status");
  }

  @Override
  public ResultMap downloadOrder(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  private String getTradeStatus(String unionpayTradeStatus) {
    if (unionpayTradeStatus == null) return UnionpayService.TRADE_STATUS.get("DEFAULT");
    String trade_status = UnionpayService.TRADE_STATUS.get(unionpayTradeStatus);
    if (trade_status == null) return UnionpayService.TRADE_STATUS.get("DEFAULT");
    return trade_status;
  }

  @Override
  public ResultMap prepareTransferInfo(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap getReqIDFromNotifyWebSync(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap getReqIDFromNotifyWebAsync(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap getReqIDFromNotifyWapSync(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap getReqIDFromNotifyWapAsync(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public ResultMap getReqIDFromNotifySDKAsync(PMap params) throws ServiceException {
    String orderId;
    if (params == null || StringUtils.isBlank(orderId = params.getString("orderId"))) {
      LOG.error("[getReqIDFromNotifySDKAsync]error:{}", params);
      return ResultMap.build(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
    }
    ResultMap resultMap = ResultMap.build();
    resultMap.addItem("reqId", orderId);
    return resultMap;
  }

  @Override
  public ResultMap getReqIDFromNotifyRefund(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    result.addItem("reqId", params.getString("orderId"));
    return result;
  }

  @Override
  public ResultMap getReqIDFromNotifyTransfer(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap handleNotifyWebSync(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap handleNotifyWebAsync(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap handleNotifyWapSync(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap handleNotifyWapAsync(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap handleNotifySDKAsync(PMap params) throws ServiceException {
    PMap notifyParams;
    ResultMap resultMap = ResultMap.build(), signCheckResult = checkSign(params, notifyParams = params.getPMap("data"));
    if (!Result.isSuccess(resultMap)) return signCheckResult;
    resultMap.addItem("reqId", notifyParams.getString("orderId"));//商户订单号
    resultMap.addItem("agencyOrderId", notifyParams.getString("queryId"));//交易查询流水号
    resultMap.addItem("tradeStatus", getTradeStatus(notifyParams.getString("respCode")));//交易状态
    resultMap.addItem("agencyPayTime",
        String.format("%s%s", LocalDate.now().getYear(), notifyParams.getString("traceTime")));//交易传输时间 MMDDHHmmss,需加上YYYY
    resultMap.addItem("trueMoney", new BigDecimal(notifyParams.getString("txnAmt"))
        .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UP).toString());//交易金额 整数分转两位小数元
    return resultMap;
  }

  @Override
  public ResultMap handleNotifyRefund(PMap params) throws ServiceException {

    ResultMap result = ResultMap.build();
    PMap notifyParams = params.getPMap("data");
    //获取商户私钥路径
    String publicCertFilePath = "e:" + params.getString("publicCertFilePath");
    //获取银联公钥
    String publicCertKey = SecretKeyUtil.loadKeyFromFile(publicCertFilePath);
    if (publicCertKey.equals("")) {
      LOG.error("[handleNotifyRefund] 银联回调获取银联公钥失败, 参数: {}", publicCertFilePath);
      return ResultMap.build(ResultStatus.THIRD_PAY_GET_KEY_ERROR);
    }
    //验证响应合法性
    String sign = (String) notifyParams.remove("signature");
    boolean signOK = SecretKeyUtil.unionRSACheckSign(notifyParams, sign, publicCertKey, CHARSET);
    if (!signOK) {
      LOG.error("[handleNotifyRefund] 银联回调返回参数签名错误, 参数: {}", params);
      result.withError(ResultStatus.THIRD_REFUND_RESPONSE_SIGN_ERROR);
      return result;
    }

    //提取关键参数
    String orderId = notifyParams.getString("orderId");
    String queryId = notifyParams.getString("queryId");
    String txnAmt = notifyParams.getString("txnAmt");
    txnAmt = String.valueOf(new BigDecimal(txnAmt).divide(new BigDecimal(100), 2, BigDecimal.ROUND_UP));
    String respCode = getTradeStatus(notifyParams.getString("respCode"));

    result.addItem("reqId", orderId);//商户网站唯一退款单号
    result.addItem("agencyRefundId", queryId);//第三方退款单号
    result.addItem("refundStatus", respCode);//交易状态
    result.addItem("refundMoney", txnAmt);//支付金额

    return result;
  }

  @Override
  public ResultMap handleNotifyTransfer(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  private void sign(TreeMap<String, String> params) {
    if (params == null || params.isEmpty()) return;
    StringBuilder sb = new StringBuilder();
    params.keySet().forEach(k -> sb.append(k).append('=').append(params.get(k)).append('&'));
    String signString = sb.substring(0, sb.length() - 1);

  }

  private static final Logger LOG = LoggerFactory.getLogger(UnionpayService.class);

  private static String VERSION = "5.0.0";

  private static String CHARSET = "UTF-8";

  private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("YYYYMMDDHHmmss");

  private static String CERTID;

  private static String ACCESSTYPE = "0";//0普通商户; 1首单机构; 2平台商户

  private static String CURRENCYCODE = "156";//rmb

  private static String SIGNMETHOD = "01";//rsa

  private static String SUCCESS_RESPONSE_CODE = "00";

  private static Map<InternalChannelType, String> channelMap = ImmutableMap.of(InternalChannelType.GATEWAY,
      ChannelType.INTERNET.getValue(), InternalChannelType.SDK, ChannelType.MOBILE.getValue());

  enum ChannelType {
    VOICE("05"), INTERNET("07"), MOBILE("08");

    private String value;

    private ChannelType(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

}
