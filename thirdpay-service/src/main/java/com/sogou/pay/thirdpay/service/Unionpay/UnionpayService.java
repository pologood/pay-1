package com.sogou.pay.thirdpay.service.Unionpay;

import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.thirdpay.biz.enums.UnionpayBizType;
import com.sogou.pay.thirdpay.biz.enums.UnionpaySubTxnType;
import com.sogou.pay.thirdpay.biz.enums.UnionpayTxnType;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayUtils;
import com.sogou.pay.thirdpay.service.ThirdpayService;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年6月15日;
//-------------------------------------------------------
@Service
public class UnionpayService implements ThirdpayService {

  private static final Logger LOG = LoggerFactory.getLogger(UnionpayService.class);
  @Value(value = "${unionpay.bill.tmpdir}")
  public static String tmpdir;
  private static HashMap<String, String> TRADE_STATUS = new HashMap<>();
  private static String VERSION = "5.0.0";
  private static String CHARSET = "UTF-8";
  private static String ACCESSTYPE = "0";//0普通商户; 1收单机构; 2平台商户
  private static String CURRENCYCODE = "156";//rmb
  private static String SIGNMETHOD = "01";//rsa

  static {
    TRADE_STATUS.put("00", OrderStatus.SUCCESS.name());//交易成功结束
    TRADE_STATUS.put("A6", OrderStatus.SUCCESS.name());//交易有缺陷成功
    TRADE_STATUS.put("DEFAULT", OrderStatus.FAILURE.name());//默认
  }

  @Override
  public ResultMap preparePayInfoAccount(PMap params) throws ServiceException {
    return preparePayInfoGatway(params);
  }

  @Override
  public ResultMap preparePayInfoGatway(PMap params) throws ServiceException {
    PMap requestPMap = getPrepayParams(params, ChannelType.INTERNET.getValue());
    if (!MapUtil.checkAllExist(requestPMap)) {
      LOG.error("[preparePayInfoGatway] empty requestMap={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    ResultMap signResult = sign(params, requestPMap);
    if (!Result.isSuccess(signResult)) return signResult;
    String returnUrl = HttpUtil.packHttpGetUrl(params.getString("payUrl"), requestPMap);
    ResultMap resultMap = ResultMap.build();
    return resultMap.addItem("returnUrl", returnUrl);
  }

  @Override
  public ResultMap preparePayInfoQRCode(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap preparePayInfoSDK(PMap params) throws ServiceException {
    PMap requestPMap = getPrepayParams(params, ChannelType.MOBILE.getValue());

    ResultMap result = doRequest(params.getString("payUrl"), params, requestPMap);
    if (!Result.isSuccess(result)) {
      LOG.error("[preparePayInfoSDK] failed, params={}", params);
      return result;
    }

    PMap responsePMap = (PMap) result.getItem("responsePMap");
    ResultMap resultMap = ResultMap.build();
    return resultMap.addItem("orderInfo", responsePMap);
  }

  @Override
  public ResultMap preparePayInfoWap(PMap params) throws ServiceException {
    PMap requestPMap = getPrepayParams(params, ChannelType.MOBILE.getValue());
    if (!MapUtil.checkAllExist(requestPMap)) {
      LOG.error("[preparePayInfoWap] empty requestMap={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }
    ResultMap signResult = sign(params, requestPMap);
    if (!Result.isSuccess(signResult)) return signResult;
    String returnUrl = HttpUtil.packHttpGetUrl(params.getString("payUrl"), requestPMap);
    ResultMap resultMap = ResultMap.build();
    return resultMap.addItem("returnUrl", returnUrl);
  }

  private PMap getPrepayParams(PMap params, String channelType) {
    PMap requestPMap = new PMap<>();

    /*必填*/
    requestPMap.put("version", VERSION);//版本号
    requestPMap.put("encoding", CHARSET);//编码方式
    requestPMap.put("certId", params.getString("md5securityKey"));//证书ID
    requestPMap.put("signMethod", SIGNMETHOD);//签名方法
    requestPMap.put("txnType", UnionpayTxnType.CONSUMPTION.getValue());//交易类型
    requestPMap.put("txnSubType", UnionpaySubTxnType.SELF_SERVICE_CONSUMPTION.getValue());//交易子类
    requestPMap.put("bizType", UnionpayBizType.B2C_GATEWAY_PAYMENT.getValue());//产品类型
    requestPMap.put("channelType", channelType);//渠道类型
    requestPMap.put("frontUrl", params.getString("pageNotifyUrl"));//前台返回商户结果时使用，前台类交易需上送
    requestPMap.put("backUrl", params.getString("serverNotifyUrl"));//后台通知地址
    requestPMap.put("accessType", ACCESSTYPE);//接入类型
    requestPMap.put("merId", params.getString("merchantNo"));//商户代码
    requestPMap.put("orderId", params.getString("serialNumber"));//商户订单号
    requestPMap.put("txnTime", params.getString("payTime"));//订单发送时间
    requestPMap.put("txnAmt", TenpayUtils.fenParseFromYuan(params.getString("orderAmount")));//交易金额
    requestPMap.put("currencyCode", CURRENCYCODE);//交易币种

    /*选填*/
    if (StringUtils.isNotBlank(params.getString("accountId")))
      requestPMap.put("accNo", params.getString("accountId"));//账号 1后台类消费交易时上送全卡号或卡号后4位;2跨行收单且收单机构收集银行卡信息时上送;3前台类交易可通过配置后返回,卡号可选上送
    String bankCode = params.getString("bankCode");
    if (StringUtils.isNotBlank(bankCode))
      requestPMap.put("issInsCode", bankCode);//1当账号类型为02-存折时需填写;2在前台类交易时填写默认银行代码,支持直接跳转到网银

    return requestPMap;
  }

  private ResultMap sendRequest(String url, PMap requestMap) {
    UnionpayHttpClient httpClient = new UnionpayHttpClient(CHARSET);
    Result response = httpClient.doPost(url, requestMap);
    if (!Result.isSuccess(response)) {
      LOG.error("[sendRequest] http request failed: {}", requestMap);
      return ResultMap.build(ResultStatus.THIRD_HTTP_ERROR);
    }
    String resContent = (String) response.getReturnValue();
    ResultMap responseMap;
    if (StringUtils.isBlank(resContent) || !ResultMap.isSuccess(responseMap = HttpUtil.extractParams(resContent))
            || MapUtils.isEmpty(responseMap.getData())) {
      LOG.error("[sendRequest] http response error: params={}, response={}", requestMap, resContent);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
    }
    return responseMap;
  }

  private ResultMap sign(PMap params, PMap signMap) {
    String privateCertKey = getKey(params, true);
    if (privateCertKey.length() == 0) {
      LOG.error("[sign] get private key failed: {}", params);
      return ResultMap.build(ResultStatus.THIRD_GET_KEY_ERROR);
    }
    String sign = SecretKeyUtil.unionRSASign(signMap, privateCertKey);
    if (sign == null) {
      LOG.error("[sign] sign failed: {}", signMap);
      return ResultMap.build(ResultStatus.THIRD_SIGN_ERROR);
    }
    signMap.put("signature", sign);
    return ResultMap.build();
  }

  private ResultMap verifySign(PMap params, PMap signMap) {
    String publicCertKey = getKey(params, false);
    if (publicCertKey.length() == 0) {
      LOG.error("[verifySign] get public key failed: {}", params);
      return ResultMap.build(ResultStatus.THIRD_GET_KEY_ERROR);
    }
    if (!SecretKeyUtil.unionRSACheckSign(signMap, (String) signMap.remove("signature"), publicCertKey)) {
      LOG.error("[verifySign] failed: {}", signMap);
      return ResultMap.build(ResultStatus.THIRD_RESPONSE_SIGN_ERROR);
    }
    return ResultMap.build();
  }

  private String getKey(PMap params, boolean isPrivate) {
    String certFilePath = getCertFilePath(params, isPrivate);
    return SecretKeyUtil.loadKeyFromFile(certFilePath);
  }

  private String getCertFilePath(PMap params, boolean isPrivate) {
    return "e:" + (isPrivate ? params.getString("privateCertFilePath") : params.getString("publicCertFilePath"));
  }

  private ResultMap doRequest(String url, PMap params, PMap requestPMap) throws ServiceException {

    ResultMap result;

    if (!MapUtil.checkAllExist(requestPMap)) {
      LOG.error("[doRequest] empty requestMap={}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PARAM_ERROR);
    }

    //签名
    result = sign(params, requestPMap);
    if (!Result.isSuccess(result)) return result;

    //请求第三方
    result = sendRequest(url, requestPMap);
    if (!Result.isSuccess(result)) return result;
    PMap responsePMap = result.getData();

    //验签
    result = verifySign(params, responsePMap);
    if (!Result.isSuccess(result)) return result;

    //请求是否成功
    String respCode = responsePMap.getString("respCode");
    if (!Objects.equals(OrderStatus.SUCCESS.name(), getTradeStatus(respCode))) {
      LOG.error("[doRequest] response error, requestPMap={}, resultPMap={}", requestPMap, responsePMap);
      result.addItem("error_code", respCode);
      result.addItem("error_msg", responsePMap.getString("respMsg"));
      result.withError(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
      return result;
    }
    return result.addItem("responsePMap", responsePMap);
  }

  @Override
  public ResultMap queryOrder(PMap params) throws ServiceException {
    PMap requestPMap = new PMap();
    //组装请求参数
    requestPMap.put("version", VERSION); //版本号
    requestPMap.put("encoding", CHARSET); //字符集编码
    requestPMap.put("certId", params.getString("md5securityKey"));//证书ID
    requestPMap.put("signMethod", SIGNMETHOD); //签名方法 目前只支持01-RSA方式证书加密
    requestPMap.put("txnType", UnionpayTxnType.TRADE_INQUIRY.getValue()); //交易类型 00-查询
    requestPMap.put("txnSubType", UnionpaySubTxnType.DEFAULT.getValue()); //交易子类型  默认00
    requestPMap.put("bizType", UnionpayBizType.DEFAULT.getValue()); //业务类型
    requestPMap.put("accessType", ACCESSTYPE); //接入类型，商户接入固定填0，不需修改
    requestPMap.put("merId", params.getString("merchantNo")); //商户号
    requestPMap.put("orderId", params.getString("serialNumber")); //我方订单号
    requestPMap.put("txnTime", params.getString("payTime"));//订单发送时间

    ResultMap result = doRequest(params.getString("queryUrl"), params, requestPMap);
    if (!Result.isSuccess(result)) {
      LOG.error("[queryOrder] failed, params={}", params);
      return result;
    }

    PMap responsePMap = (PMap) result.getItem("responsePMap");
    return result.addItem("order_state", getTradeStatus(responsePMap.getString("origRespCode")));
  }

  @Override
  public ResultMap refundOrder(PMap params) throws ServiceException {
    PMap requestPMap = new PMap();
    //组装请求参数
    requestPMap.put("version", VERSION); //版本号
    requestPMap.put("encoding", CHARSET); //字符集编码
    requestPMap.put("certId", params.getString("md5securityKey"));//证书ID
    requestPMap.put("signMethod", SIGNMETHOD); //签名方法 目前只支持01-RSA方式证书加密
    requestPMap.put("txnType", UnionpayTxnType.REFUND.getValue()); //交易类型 04-退货
    requestPMap.put("txnSubType", UnionpaySubTxnType.DEFAULT.getValue()); //交易子类型  默认00
    requestPMap.put("bizType", UnionpayBizType.DEFAULT.getValue()); //业务类型
    requestPMap.put("channelType", ChannelType.MOBILE.getValue()); //渠道类型，07-PC，08-手机
    requestPMap.put("merId", params.getString("merchantNo")); //商户号
    requestPMap.put("accessType", ACCESSTYPE); //接入类型，商户接入固定填0，不需修改
    requestPMap.put("orderId", params.getString("refundSerialNumber")); //商户退款单号，8-40位数字字母，不能含“-”或“_”
    requestPMap.put("txnTime", params.getString("refundReqTime")); //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
    requestPMap.put("currencyCode", CURRENCYCODE); //交易币种（境内商户一般是156 人民币）
    String refundAmount = TenpayUtils.fenParseFromYuan(params.getString("refundAmount"));
    requestPMap.put("txnAmt", refundAmount); //退货金额，单位分
    requestPMap.put("backUrl", params.getString("refundNotifyUrl")); //后台通知地址
    requestPMap.put("origQryId", params.getString("agencySerialNumber")); //原消费交易返回的的queryId

    ResultMap result = doRequest(params.getString("refundUrl"), params, requestPMap);
    if (!Result.isSuccess(result)) {
      LOG.error("[refundOrder] failed, params={}", params);
      return result;
    }

    PMap responsePMap = (PMap) result.getItem("responsePMap");
    return result.addItem("third_refund_id", responsePMap.getString("queryId"));
  }

  @Override
  public ResultMap queryRefundOrder(PMap params) throws ServiceException {
    PMap requestPMap = new PMap();
    //组装请求参数
    requestPMap.put("version", VERSION); //版本号
    requestPMap.put("encoding", CHARSET); //字符集编码
    requestPMap.put("certId", params.getString("md5securityKey"));//证书ID
    requestPMap.put("signMethod", SIGNMETHOD); //签名方法 目前只支持01-RSA方式证书加密
    requestPMap.put("txnType", UnionpayTxnType.TRADE_INQUIRY.getValue()); //交易类型 00-查询
    requestPMap.put("txnSubType", UnionpaySubTxnType.DEFAULT.getValue()); //交易子类型  默认00
    requestPMap.put("bizType", UnionpayBizType.DEFAULT.getValue()); //业务类型
    requestPMap.put("accessType", ACCESSTYPE); //接入类型，商户接入固定填0，不需修改
    requestPMap.put("merId", params.getString("merchantNo")); //商户号
    requestPMap.put("queryId", params.getString("agencySerialNumber")); //原消费交易返回的的queryId

    ResultMap result = doRequest(params.getString("queryRefundUrl"), params, requestPMap);
    if (!Result.isSuccess(result)) {
      LOG.error("[queryRefundOrder] failed, params={}", params);
      return result;
    }

    PMap responsePMap = (PMap) result.getItem("responsePMap");
    return result.addItem("refund_status", getTradeStatus(responsePMap.getString("origRespCode")));
  }

  @Override
  public ResultMap downloadOrder(PMap params) throws ServiceException {
    PMap requestPMap = new PMap();

    Date checkDate = (Date) params.get("checkDate");
    String unionpayCheckDate = DateUtil.format(checkDate, DateUtil.DATE_FORMAT_DAY_OF_YEAR);

    //组装请求参数
    requestPMap.put("version", "5.0.0"); //版本号
    requestPMap.put("encoding", UnionpayService.CHARSET); //字符集编码
    requestPMap.put("certId", params.getString("md5securityKey"));//证书ID
    requestPMap.put("signMethod", "01"); //签名方法 目前只支持01-RSA方式证书加密
    requestPMap.put("txnType", "76"); //交易类型 76-对账单
    requestPMap.put("txnSubType", "01"); //交易子类型  下载对账单
    requestPMap.put("bizType", "000000"); //业务类型
    requestPMap.put("merId", params.getString("merchantNo")); //商户号
    requestPMap.put("accessType", "0"); //接入类型，商户接入固定填0，不需修改
    requestPMap.put("settleDate", unionpayCheckDate); //对账日期，一年内MMdd
    requestPMap.put("txnTime", DateUtil.formatShortTime(new Date())); //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
    requestPMap.put("fileType", "00"); //对账文件类型 00-zip

    ResultMap result = doRequest(params.getString("downloadUrl"), params, requestPMap);
    if (!Result.isSuccess(result)) {
      LOG.error("[downloadOrder] failed, params={}", params);
      return result;
    }

    PMap responsePMap = (PMap) result.getItem("responsePMap");
    String fileType = responsePMap.getString("fileType");
    String fileName = responsePMap.getString("fileName");
    String fileContent = responsePMap.getString("fileContent");

    try {
      //保存对账文件
      File compressedfile = new File(new File(tmpdir), fileName + "." + fileType);
      OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(compressedfile), UnionpayService.CHARSET);
      BufferedWriter bw = new BufferedWriter(osw);
      bw.write(fileContent);
      bw.flush();
      bw.close();
      osw.close();
      //解压对账文件
      byte[] buffer = new byte[1024];
      ZipFile zipFile = new ZipFile(compressedfile);
      Enumeration<ZipArchiveEntry> enums = zipFile.getEntries();
      while (enums.hasMoreElements()) {
        ZipArchiveEntry entry = enums.nextElement();
        File billFile = new File(new File(tmpdir), entry.getName());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(billFile));
        BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
        int len = 0;
        while ((len = bis.read(buffer)) >= 0) {
          bos.write(buffer, 0, len);
        }
        bos.flush();
        bos.close();
        bis.close();
      }
    } catch (Exception ex) {
      LOG.error("[downloadOrder] failed, params={}, {}", JSONUtil.Bean2JSON(params), ex);
      return ResultMap.build(ResultStatus.SAVE_BILL_FAILED);
    }
    return ResultMap.build();
  }

  private String getTradeStatus(String unionpayTradeStatus) {
    if (unionpayTradeStatus == null) return UnionpayService.TRADE_STATUS.get("DEFAULT");
    String trade_status = UnionpayService.TRADE_STATUS.get(unionpayTradeStatus);
    if (trade_status == null) return UnionpayService.TRADE_STATUS.get("DEFAULT");
    return trade_status;
  }

  @Override
  public ResultMap prepareTransferInfo(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap queryTransfer(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap queryTransferRefund(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap getReqIDFromNotifyWebSync(PMap params) throws ServiceException {
    return getReqIDFromNotifySDKAsync(params);
  }

  @Override
  public ResultMap getReqIDFromNotifyWebAsync(PMap params) throws ServiceException {
    return getReqIDFromNotifySDKAsync(params);
  }

  @Override
  public ResultMap getReqIDFromNotifyWapSync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap getReqIDFromNotifyWapAsync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

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
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap handleNotifyWebSync(PMap params) throws ServiceException {
    return handleNotifySDKAsync(params);
  }

  @Override
  public ResultMap handleNotifyWebAsync(PMap params) throws ServiceException {
    return handleNotifySDKAsync(params);
  }

  @Override
  public ResultMap handleNotifyWapSync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap handleNotifyWapAsync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap handleNotifySDKAsync(PMap params) throws ServiceException {
    PMap notifyParams;
    ResultMap resultMap = ResultMap.build(), signCheckResult = verifySign(params, notifyParams = params.getPMap("data"));
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
    //获取银联公钥路径
    String publicCertFilePath = "e:" + params.getString("publicCertFilePath");
    //获取银联公钥
    String publicCertKey = SecretKeyUtil.loadKeyFromFile(publicCertFilePath);
    if (publicCertKey.equals("")) {
      LOG.error("[handleNotifyRefund] 银联回调获取银联公钥失败, 参数: {}", publicCertFilePath);
      return ResultMap.build(ResultStatus.THIRD_PAY_GET_KEY_ERROR);
    }
    //验证响应合法性
    String sign = (String) notifyParams.remove("signature");
    if (!SecretKeyUtil.unionRSACheckSign(notifyParams, sign, publicCertKey)) {
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
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

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

  /*
      全渠道平台网银支付银行前置模式——支持的标准网关银行列表     
  ICBC 工商银行
  ABC 农业银行
  BOC 中国银行（大额）
  BOCSH 中国银行
  CCB 建设银行
  CMB 招商银行
  SPDB 浦发银行
  GDB 广发银行
  BOCOM 交通银行
  CNCB 中信银行
  CMBC 民生银行
  CIB 兴业银行
  CEB 光大银行
  HXB 华夏银行
  BOS 上海银行
  SRCB 上海农商
  PSBC 邮政储蓄
  BCCB 北京银行
  BRCB 北京农商
  PAB 平安银行
  
      全渠道平台网银支付银行前置模式——支持的独立借记卡网关银行列表
  ICBCD 工商银行
  CCBD 建设银行
  CMBD 招商银行
  SPDBD 上海浦东发展银行
  GDBD 广发银行
  PSBCD 邮政储蓄银行
  CMBCD 民生银行
  CEBD 光大银行
  HXB 华夏银行
  BOEAD 东亚银行
  ABCD 中国农业银行
  
      全渠道平台银行卡开通支持银行列表
  ABC 中国农业银行
  AHRCU 安徽省农村信用社
  AYB 安阳市商业银行
  BANKOFAS 鞍山银行
  BCCB 北京银行
  BDBK 保定市商业银行
  BEAI 东亚银行
  BOBBG 广西北部湾银行
  BOC 中国银行
  BOCOM 交通银行
  BOCZ 沧州银行
  BOFS 抚顺银行
  BOHC 渤海银行
  BOHH 新疆汇和银行
  BOHLD 葫芦岛市商业银行
  BOIMC 内蒙古银行
  BOJS 江苏银行
  BOLY 洛阳银行
  BOLZ 柳州银行
  BOQHD 秦皇岛市商业银行
  BORZ 日照银行
  BOS 上海银行
  BOSZ 苏州银行
  BOSZS 石嘴山银行股份有限公司
  BOWH 乌海银行
  BOZK 周口市商业银行
  BQH 青海银行
  BRCB 北京农村商业银行
  BSB 包商银行
  BTCB 包商银行
  CBCRB 重庆北碚稠州村镇银行
  CBOA 安顺市商业银行
  CBZZ 郑州银行
  CCB 中国建设银行
  CCCB 长沙银行
  CCQTGB 重庆三峡银行
  CDB 承德银行
  CDCB 成都银行
  CDRCB 成都农商银行
  CEB 光大银行
  CIB 兴业银行深圳分行
  CITIB 花旗银行
  CJCCB 江苏长江商业银行
  CMB 招商银行（网银）
  CMBC 中国民生银行深圳分行
  CNCB 中信银行信用卡
  CQCB 重庆银行
  CQRCB 重庆农村商业银行股份有限公司
  CRB 珠海华润银行
  CSRCBANK 常熟农商银行
  CYCB 朝阳银行
  CZCB 浙江稠州商业银行
  CZCCB 长治商行
  CZSB 浙商银行
  DGCB 东莞银行
  DGCU 东莞农村商业银行
  DLCB 大连银行
  DYCB 东营商行
  DYCC 德阳银行
  DZBCHINA 德州市商业银行
  EBCL 恒丰银行
  FJHXB 福建海峡银行
  FJNX 福建农信
  FTYZB 深圳福田银座村镇银行
  FUXINBANK 阜新银行股份有限公司
  GDB 广东发展银行
  GDRCC 广东农村信用社
  GHCRB 广州花都稠州村镇银行
  GLB 桂林银行
  GRCB 广州农村商业银行
  GSRCU 甘肃省农村信用社
  GXNX 广西农村信用社联合社
  GYCB 贵阳银行
  GZCB 广州银行
  GZCCB 赣州银行
  HANABANK 韩亚银行
  HBC 湖北银行
  HBCB 哈尔滨银行
  HBSB 鹤壁银行
  HBXH 湖北省农村信用社联合社
  HDCB 邯郸银行
  HEBB 河北银行
  HEBNX 河北省农村信用社
  HKBCHINA 汉口银行
  HNB 海南农信
  HNRCC 湖南农信社
  HSB 衡水市商业银行
  HSCB 徽商银行
  HXB 华夏银行
  HZCB 杭州银行
  HZCCB 湖州银行
  ICBC 中国工商银行
  IMRCC 内蒙古农信社
  JCCB 晋城市商业银行
  JHB 金华银行
  JJCCB 九江银行
  JLCB 吉林银行股份有限公司
  JLPRCU 吉林省农村信用社
  JNBANK 济宁银行股份有限公司
  JNRCB 江南农村商业银行
  JSB 晋商银行
  JSRCU 江苏省农村信用社联合社
  JXCCB 嘉兴银行
  JXNXS 江西农信
  JYRB 江苏江阴农村商业银行
  JZCB 锦州银行
  JZCCB 晋中市商业银行
  KLB 昆仑银行
  KMCB 富滇银行
  KSRB 昆山农村商业银行
  LJB 龙江银行股份有限公司
  LSB 临商银行
  LSBANKCHINA 莱芜市商业银行
  LSCCB 乐山市商业银行
  LSZSH 凉山州商业银行
  LUOHEBANK 漯河商行
  LZCB 兰州银行
  LZCCB 泸州市商业银行
  MIANYANGCCB 绵阳市商业银行
  MXHCB 梅县客家村镇银行
  NBCB 宁波银行
  NCCB 南昌银行
  NCCC 南充商行
  NJCB 南京银行
  ORDOSB 鄂尔多斯银行
  PAB 平安银行
  PJCB 盘锦市商业银行
  PSBC 邮政储蓄银行
  PZHCCB 攀枝花市商业银行
  QDCB 青岛银行
  QHRCU 青海省农村信用社联合社
  QJCCB 曲靖市商业银行
  QLB 齐鲁银行
  QZCCB 泉州银行
  RCCOSD 山东省农村信用社-新
  SCB 渣打银行
  SCRCU 四川省农村信用合作社
  SDB 深圳发展银行
  SDEBANK 佛山市顺德区农村信用合作联社
  SDRCB 顺德农商
  SG 法国兴业银行（中国）有限公司
  SHBC 新韩银行
  SMXB 三门峡市商业银行
  SNCCB 遂宁市商业银行
  SPDB 上海浦东发展银行
  SQB 商丘银行
  SRB 上饶银行
  SRCB 上海农商行
  SXCCB 绍兴银行
  SXRCU 山西省农村信用社
  SZRB 深圳农村商业银行
  TACCB 泰安市商业银行
  TCRCB 太仓农村商业银行
  TJBHB 天津滨海农村商业银行
  TJCB 天津银行
  TLCB 浙江泰隆商业银行
  TRCB 天津农村商业银行
  TSCCB 唐山市商业银行
  TZB 台州市商业银行
  UOB 大华银行
  UQCB 乌鲁木齐市商业银行
  WFCB 潍坊银行
  WHCCB 威海市商业银行
  WJRCB 吴江农村商业银行
  WRCB 无锡农村商业银行
  WZCB 温州银行
  XCCB 许昌市商业银行
  XJKCCB 库尔勒市商业银行
  XMCCB 厦门银行股份有限公司
  XTBK 邢台银行
  YACCB 雅安市商业银行
  YBCCB 宜宾市商业银行
  YCCCB 宁夏银行
  YDRCB 山西尧都农村商业银行
  YKCB 营口市商业银行
  YNRCC 云南省农村信用社
  YQCCB 阳泉市商业银行
  YRRCB 黄河农村商业银行
  YTCB 烟台市商业银行
  YXCCB 玉溪市商业银行
  YZRB 宁波鄞州农村合作银行
  ZCCB 齐商银行
  ZGCB 自贡市商业银行
  ZJKCCB 张家口市商业银行
  ZJMTCB 浙江民泰商业银行
  ZJRB 张家港农村商业银行
  ZYCCB 遵义市商业银行股份有限公司
  tzunionpay 泰州银联商务
  */

}
