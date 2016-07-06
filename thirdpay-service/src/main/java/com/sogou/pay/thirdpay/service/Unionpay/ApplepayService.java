package com.sogou.pay.thirdpay.service.Unionpay;

import com.sogou.pay.common.model.StdPayRequest;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.thirdpay.biz.enums.UnionpayBizType;
import com.sogou.pay.thirdpay.biz.enums.UnionpaySubTxnType;
import com.sogou.pay.thirdpay.biz.enums.UnionpayTxnType;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;



@Service
public class ApplepayService extends UnionpayService {

  @Override
  protected PMap<String, String> getPrepayParams(StdPayRequest params, String channelType) {
    PMap<String, String> requestPMap = new PMap<>();

    /*必填*/
    requestPMap.put("version", VERSION);//版本号
    requestPMap.put("encoding", CHARSET);//编码方式
    requestPMap.put("certId", params.getMd5Key());//证书ID
    requestPMap.put("signMethod", SIGNMETHOD);//签名方法
    requestPMap.put("txnType", UnionpayTxnType.CONSUMPTION.getValue());//交易类型
    requestPMap.put("txnSubType", UnionpaySubTxnType.SELF_SERVICE_CONSUMPTION.getValue());//交易子类
    requestPMap.put("bizType", UnionpayBizType.APPLEPAY_PAYMENT.getValue());//产品类型
    requestPMap.put("channelType", channelType);//渠道类型
    requestPMap.put("frontUrl", params.getPageNotifyUrl());//前台返回商户结果时使用，前台类交易需上送
    requestPMap.put("backUrl", params.getServerNotifyUrl());//后台通知地址
    requestPMap.put("accessType", ACCESSTYPE);//接入类型
    requestPMap.put("merId", params.getMerchantId());//商户代码
    requestPMap.put("orderId", params.getPayId());//商户订单号
    requestPMap.put("txnTime", params.getPayTimeString());//订单发送时间
    requestPMap.put("txnAmt", TenpayUtils.fenParseFromYuan(params.getOrderAmount()));//交易金额
    requestPMap.put("currencyCode", CURRENCYCODE);//交易币种

    /*选填*/
    if (StringUtils.isNotBlank(params.getAccountId()))
      requestPMap.put("accNo", params.getAccountId());//账号 1后台类消费交易时上送全卡号或卡号后4位;2跨行收单且收单机构收集银行卡信息时上送;3前台类交易可通过配置后返回,卡号可选上送
    //bankCode暂时不起作用，需要开通银联的网银前置
    String bankCode = params.getBankCode();
    if (StringUtils.isNotBlank(bankCode))
      requestPMap.put("issInsCode", bankCode);//1当账号类型为02-存折时需填写;2在前台类交易时填写默认银行代码,支持直接跳转到网银

    return requestPMap;
  }

}
