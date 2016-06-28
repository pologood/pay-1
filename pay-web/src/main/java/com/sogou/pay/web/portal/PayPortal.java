package com.sogou.pay.web.portal;

import com.google.common.collect.ImmutableMap;
import com.sogou.pay.common.Model.StdPayRequest;
import com.sogou.pay.common.Model.StdPayRequest.Payment;
import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.service.enums.AgencyCode;
import com.sogou.pay.thirdpay.service.Alipay.AlipayService;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayService;
import com.sogou.pay.thirdpay.service.ThirdpayService;
import com.sogou.pay.thirdpay.service.Unionpay.UnionpayService;
import com.sogou.pay.thirdpay.service.Wechat.WechatService;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiepeidong on 2016/1/19.
 */
@Component
public class PayPortal {

  private static Logger log = LoggerFactory.getLogger(PayPortal.class);

  @Autowired
  private AlipayService alipayService;

  @Autowired
  private TenpayService tenpayService;

  @Autowired
  private WechatService wechatService;

  @Autowired
  private UnionpayService unionpayService;

  private HashMap<String, ThirdpayService> serviceHashMap;

  @Autowired
  public void init() {
    serviceHashMap = new HashMap<>();
    serviceHashMap.put(AgencyCode.ALIPAY.name(), alipayService);
    serviceHashMap.put(AgencyCode.TENPAY.name(), tenpayService);
    serviceHashMap.put(AgencyCode.WECHAT.name(), wechatService);
    serviceHashMap.put(AgencyCode.UNIONPAY.name(), unionpayService);
    serviceHashMap.put(AgencyCode.TEST_ALIPAY.name(), alipayService);
    serviceHashMap.put(AgencyCode.TEST_TENPAY.name(), tenpayService);
    serviceHashMap.put(AgencyCode.TEST_WECHAT.name(), wechatService);
    serviceHashMap.put(AgencyCode.TEST_UNIONPAY.name(), unionpayService);
  }

  public ResultMap preparePay(StdPayRequest params) {

    ResultMap result = ResultMap.build();

    //验证共同参数是否为空
    if (StringUtil.isEmpty(params.getAgencyCode(), params.getPayment().name(), params.getOrderAmountString(),
        params.getMd5Key())) {
      log.error("[preparePay] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_PARAM_ERROR);
    }
    //验证金额是否大于0
    BigDecimal oAmount = params.getOrderAmount();
    if (oAmount.compareTo(new BigDecimal(0)) != 1) {
      log.error("[preparePay] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_PARAM_ERROR);
    }
    ThirdpayService thirdpayService = serviceHashMap.get(params.getAgencyCode());
    try {
      Pay payment = PAYMENTMAP.get(params.getPayment());
      if (payment == null) {
        log.error("[preparePay] payChannel not exists, params={}", JSONUtil.Bean2JSON(params));
        result.withError(ResultStatus.THIRD_PAY_CHANNEL_NOT_EXIST);
      } else result = payment.pay(thirdpayService, params);
    } catch (ServiceException e) {
      log.error("[preparePay] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      result.withError(e.getStatus());
    } catch (Exception e) {
      log.error("[preparePay] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      result.withError(ResultStatus.THIRD_ERROR);
    }
    return result;
  }

  public ResultMap refundOrder(PMap params) {
    ResultMap result = ResultMap.build();

    //验证共同参数是否为空
    if (StringUtil.isEmpty(params.getString("agencyCode"), params.getString("md5securityKey"),
        params.getString("refundAmount"), params.getString("totalAmount"))) {
      log.error("[refundOrder] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_PARAM_ERROR);
    }
    //验证金额是否大于0
    BigDecimal refundAmount = new BigDecimal(params.getString("refundAmount"));
    BigDecimal totalAmount = new BigDecimal(params.getString("totalAmount"));
    int compareRefundAmount = refundAmount.compareTo(new BigDecimal(0));
    int compareTotalAmount = totalAmount.compareTo(new BigDecimal(0));
    if (compareRefundAmount != 1 || compareTotalAmount != 1) {
      log.error("[refundOrder] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_PARAM_ERROR);
    }

    //根据不同支付机构调用退款接口
    String agencyCode = params.getString("agencyCode");
    ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);

    try {
      result = thirdpayService.refundOrder(params);
    } catch (ServiceException e) {
      log.error("[refundOrder] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return (ResultMap) result.withError(e.getStatus());
    } catch (Exception e) {
      log.error("[refundOrder] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return (ResultMap) result.withError(ResultStatus.THIRD_ERROR);
    }
    return result;
  }

  public ResultMap queryOrder(PMap params) {
    ResultMap result = ResultMap.build();

    //验证共同参数是否为空
    if (StringUtil.isEmpty(params.getString("agencyCode"), params.getString("md5securityKey"))) {
      log.error("[queryOrder] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_PARAM_ERROR);
    }

    //根据不同支付机构调用退款接口
    String agencyCode = params.getString("agencyCode");
    ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
    try {
      result = thirdpayService.queryOrder(params);
    } catch (ServiceException e) {
      log.error("[queryOrder] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return result.build(e.getStatus());
    } catch (Exception e) {
      log.error("[queryOrder] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return result.build(ResultStatus.THIRD_ERROR);
    }
    return result;
  }

  public ResultMap queryRefund(PMap params) {
    ResultMap result = ResultMap.build();

    //验证共同参数是否为空
    if (StringUtil.isEmpty(params.getString("agencyCode"), params.getString("md5securityKey"))) {
      log.error("[queryRefund] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_PARAM_ERROR);
    }

    //根据不同支付机构调用退款接口
    String agencyCode = params.getString("agencyCode");
    ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
    try {
      result = thirdpayService.queryRefundOrder(params);
    } catch (ServiceException e) {
      log.error("[queryRefund] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return result.build(e.getStatus());
    } catch (Exception e) {
      log.error("[queryRefund] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return result.build(ResultStatus.THIRD_ERROR);
    }
    return result;
  }

  public ResultMap getReqIDFromNotify(PMap params) {
    ResultMap result = ResultMap.build();
    String agencyCode = params.getString("agencyCode");
    ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
    String notifyType = params.getString("notifyType");
    try {
      switch (notifyType) {
        case "WEB_SYNC":
          result = thirdpayService.getReqIDFromNotifyWebSync(params.getPMap("data"));
          break;
        case "WEB_ASYNC":
          result = thirdpayService.getReqIDFromNotifyWebAsync(params.getPMap("data"));
          break;
        case "WAP_SYNC":
          result = thirdpayService.getReqIDFromNotifyWapSync(params.getPMap("data"));
          break;
        case "WAP_ASYNC":
          result = thirdpayService.getReqIDFromNotifyWapAsync(params.getPMap("data"));
          break;
        case "SDK_ASYNC":
          result = thirdpayService.getReqIDFromNotifySDKAsync(params.getPMap("data"));
          break;
        case "REFUND":
          result = thirdpayService.getReqIDFromNotifyRefund(params.getPMap("data"));
          break;
        case "TRANSFER":
          result = thirdpayService.getReqIDFromNotifyTransfer(params.getPMap("data"));
          break;
      }
    } catch (ServiceException e) {
      log.error("[getReqIDFromNotify] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return result.build(e.getStatus());
    } catch (Exception e) {
      log.error("[getReqIDFromNotify] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return result.build(ResultStatus.THIRD_ERROR);
    }
    return result;

  }

  public ResultMap handleNotify(PMap params) {
    ResultMap result = ResultMap.build();
    String agencyCode = params.getString("agencyCode");
    ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
    String notifyType = params.getString("notifyType");
    try {
      switch (notifyType) {
        case "WEB_SYNC":
          result = thirdpayService.handleNotifyWebSync(params);
          break;
        case "WEB_ASYNC":
          result = thirdpayService.handleNotifyWebAsync(params);
          break;
        case "WAP_SYNC":
          result = thirdpayService.handleNotifyWapSync(params);
          break;
        case "WAP_ASYNC":
          result = thirdpayService.handleNotifyWapAsync(params);
          break;
        case "SDK_ASYNC":
          result = thirdpayService.handleNotifySDKAsync(params);
          break;
        case "REFUND":
          result = thirdpayService.handleNotifyRefund(params);
          break;
        case "TRANSFER":
          result = thirdpayService.handleNotifyTransfer(params);
          break;
      }
    } catch (ServiceException e) {
      log.error("[handleNotify] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return result.build(e.getStatus());
    } catch (Exception e) {
      log.error("[handleNotify] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return result.build(ResultStatus.THIRD_ERROR);
    }
    return result;
  }

  @FunctionalInterface
  public interface Pay {

    public ResultMap<?> pay(ThirdpayService service, StdPayRequest param) throws ServiceException;

  }

  private static final Map<Payment, Pay> PAYMENTMAP = ImmutableMap.of(Payment.PC_ACCOUNT,
      (service, request) -> service.preparePayInfoAccount(request), Payment.PC_GATEWAY,
      (service, request) -> service.preparePayInfoGatway(request), Payment.QRCODE,
      (service, request) -> service.preparePayInfoQRCode(request), Payment.MOBILE_SDK,
      (service, request) -> service.preparePayInfoSDK(request), Payment.MOBILE_WAP,
      (service, request) -> service.preparePayInfoWap(request));

}
