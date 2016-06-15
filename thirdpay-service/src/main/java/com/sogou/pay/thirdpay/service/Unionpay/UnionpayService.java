/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.thirdpay.service.Unionpay;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.client.HttpService;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayUtils;
import com.sogou.pay.thirdpay.service.ThirdpayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年6月15日;
//-------------------------------------------------------
public class UnionpayService implements ThirdpayService {
  private static final Logger log = LoggerFactory.getLogger(UnionpayService.class);
  public static final String INPUT_CHARSET = "UTF-8";                           // 字符编码格式 UTF-8

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

  @Override
  public ResultMap preparePayInfoSDK(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap preparePayInfoWap(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap queryOrder(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap refundOrder(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    PMap requestPMap = new PMap();
    //组装请求参数
    requestPMap.put("version", "5.0.0");               //版本号
    requestPMap.put("encoding", UnionpayService.INPUT_CHARSET);             //字符集编码
    requestPMap.put("signMethod", "01");                        //签名方法 目前只支持01-RSA方式证书加密
    requestPMap.put("txnType", "04");                           //交易类型 04-退货
    requestPMap.put("txnSubType", "00");                        //交易子类型  默认00
    requestPMap.put("bizType", "000201");                       //业务类型
    requestPMap.put("channelType", "08");                       //渠道类型，07-PC，08-手机
    requestPMap.put("merId", params.getString("merchantNo"));                //商户号
    requestPMap.put("accessType", "0");                         //接入类型，商户接入固定填0，不需修改
    requestPMap.put("orderId", params.getString("refundSerialNumber"));          //商户退款单号，8-40位数字字母，不能含“-”或“_”
    requestPMap.put("txnTime", DateUtil.format(params.getDate("refundReqTime"), DateUtil.DATE_FORMAT_SECOND_SHORT));      //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
    requestPMap.put("currencyCode", "156");                     //交易币种（境内商户一般是156 人民币）
    String refundAmount = TenpayUtils.fenParseFromYuan(params.getString("refundAmount"));
    requestPMap.put("txnAmt", refundAmount);                          //退货金额，单位分
    requestPMap.put("backUrl", params.getString("refundNotifyUrl"));               //后台通知地址
    requestPMap.put("origQryId", params.getString("serialNumber"));      //原消费交易返回的的queryId

    if (!MapUtil.checkAllExist(requestPMap)) {
      log.error("[refundOrder] 银联退款参数错误, 参数: {}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_REFUND_PARAM_ERROR);
    }
    //获取商户私钥路径
    String privateCertFilePath = "e:" + params.getString("privateCertFilePath");
    //获取商户私钥
    String privateCertKey = SecretKeyUtil.loadKeyFromFile(privateCertFilePath);
    if (privateCertKey.equals("")) {
      log.error("[refundOrder] 银联退款获取第三方支付账户密钥失败, 参数: {}", params);
      return ResultMap.build(ResultStatus.THIRD_PAY_GET_KEY_ERROR);
    }
    //签名
    String
            sign =
            SecretKeyUtil.unionRSASign(requestPMap, privateCertKey, UnionpayService.INPUT_CHARSET);
    if (sign == null) {
      log.error("[refundOrder] 银联退款签名失败, 参数: {}", requestPMap);
      return ResultMap.build(ResultStatus.THIRD_PAY_SIGN_ERROR);
    }
    requestPMap.put("signature", sign);

    Result httpResponse = HttpService.getInstance().doPost(params.getString("refundUrl"), requestPMap,
            UnionpayService.INPUT_CHARSET, null);
    if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
      log.error("[refundOrder] 银联退款HTTP请求失败, 参数: {}", requestPMap);
      result.withError(ResultStatus.THIRD_REFUND_HTTP_ERROR);
      return result;
    }
    String resContent = (String) httpResponse.getReturnValue();
    //解析响应
    if (resContent == null) {
      log.error("[refundOrder] 银联退款返回参数异常, 参数: {}", requestPMap);
      result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
      return result;
    }
    PMap resultPMap = HttpUtil.extractUrlParams(resContent).getData();
    sign = (String) resultPMap.remove("signature");
    //验证响应合法性
    boolean
            signOK =
            SecretKeyUtil
                    .unionRSACheckSign(resultPMap, sign, privateCertKey, UnionpayService.INPUT_CHARSET);
    if (!signOK) {
      log.error("[refundOrder] 银联退款返回参数签名错误, 参数: {}, 返回: {}", resultPMap, resultPMap);
      result.withError(ResultStatus.THIRD_REFUND_RESPONSE_SIGN_ERROR);
      return result;
    }
    String respCode = resultPMap.getString("respCode");
    if (!respCode.equals("00") && !respCode.equals("A6")) {
      log.error("[refundOrder] 银联退款返回参数异常, respCode!=00/A6，参数: {}, 返回: {}", requestPMap, resultPMap);
      result.addItem("error_code", respCode);
      result.addItem("error_msg", resultPMap.getString("respMsg"));
      result.withError(ResultStatus.THIRD_REFUND_RESPONSE_PARAM_ERROR);
      return result;
    }
    return result.addItem("third_refund_id", resultPMap.getString("queryId"));
  }

  @Override
  public ResultMap queryRefundOrder(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap downloadOrder(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

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

  @Override
  public ResultMap getReqIDFromNotifySDKAsync(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap getReqIDFromNotifyRefund(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

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
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap handleNotifyRefund(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ResultMap handleNotifyTransfer(PMap params) throws ServiceException {
    // TODO Auto-generated method stub
    return null;

  }

}