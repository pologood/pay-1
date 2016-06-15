package com.sogou.pay.web.portal;

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
import com.sogou.pay.thirdpay.service.Wechat.WechatService;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by xiepeidong on 2016/1/19.
 */
@Component
public class PayPortal {

  private static Logger log = LoggerFactory.getLogger(PayPortal.class);

  public static final int PC_ACCOUNT = 0;//账户支付
  public static final int PC_GATEWAY = 1;//网关(网银)支付
  public static final int QRCODE = 2;//扫码支付
  public static final int MOBILE_SDK = 3;//SDK支付
  public static final int MOBILE_WAP = 4;//WAP支付

  @Autowired
  private AlipayService alipayService;

  @Autowired
  private TenpayService tenpayService;

  @Autowired
  private WechatService wechatService;

  private HashMap<String, ThirdpayService> serviceHashMap;

  @Autowired
  public void init() {
    serviceHashMap = new HashMap<>();
    serviceHashMap.put(AgencyCode.ALIPAY.name(), alipayService);
    serviceHashMap.put(AgencyCode.TENPAY.name(), tenpayService);
    serviceHashMap.put(AgencyCode.WECHAT.name(), wechatService);
    serviceHashMap.put(AgencyCode.TEST_ALIPAY.name(), alipayService);
    serviceHashMap.put(AgencyCode.TEST_TENPAY.name(), tenpayService);
    serviceHashMap.put(AgencyCode.TEST_WECHAT.name(), wechatService);
  }

  public ResultMap preparePay(PMap params) {

    ResultMap result = ResultMap.build();

    //验证共同参数是否为空
    if (StringUtil.isEmpty(params.getString("agencyCode"), params.getString("payChannel"),
            params.getString("orderAmount"), params.getString("md5securityKey"))) {
      log.error("[preparePay] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_PAY_PARAM_ERROR);
    }
    //验证金额是否大于0
    BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
    if (oAmount.compareTo(new BigDecimal(0)) != 1) {
      log.error("[preparePay] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_PAY_PARAM_ERROR);
    }

    String agencyCode = params.getString("agencyCode");
    ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
    //根据不同支付机构请求不接口获取支付参数
    int payChannle = params.getInt("payChannel");
    try {
      switch (payChannle) {
        case PayPortal.PC_ACCOUNT:
          result = thirdpayService.preparePayInfoAccount(params);
          break;
        case PayPortal.PC_GATEWAY:
          result = thirdpayService.preparePayInfoGatway(params);
          break;
        case PayPortal.QRCODE:
          result = thirdpayService.preparePayInfoQRCode(params);
          break;
        case PayPortal.MOBILE_SDK:
          result = thirdpayService.preparePayInfoSDK(params);
          break;
        case PayPortal.MOBILE_WAP:
          result = thirdpayService.preparePayInfoWap(params);
          break;
        default:
          log.error("[preparePay] payChannel not exists, params={}", JSONUtil.Bean2JSON(params));
          result.build(ResultStatus.THIRD_PAY_CHANNEL_NOT_EXIST);
      }
    } catch (ServiceException e) {
      log.error("[preparePay] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      result.withError(e.getStatus());
    } catch (Exception e) {
      log.error("[preparePay] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      result.withError(ResultStatus.THIRD_PAY_ERROR);
    }
    return result;
  }

  public ResultMap refundOrder(PMap params) {
    ResultMap result = ResultMap.build();

    //验证共同参数是否为空
    if (StringUtil.isEmpty(params.getString("agencyCode"),
            params.getString("md5securityKey"), params.getString("refundAmount"),
            params.getString("totalAmount"))) {
      log.error("[refundOrder] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_REFUND_PARAM_ERROR);
    }
    //验证金额是否大于0
    BigDecimal refundAmount = new BigDecimal(params.getString("refundAmount"));
    BigDecimal totalAmount = new BigDecimal(params.getString("totalAmount"));
    int compareRefundAmount = refundAmount.compareTo(new BigDecimal(0));
    int compareTotalAmount = totalAmount.compareTo(new BigDecimal(0));
    if (compareRefundAmount != 1 || compareTotalAmount != 1) {
      log.error("[refundOrder] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_REFUND_PARAM_ERROR);
    }

    //根据不同支付机构调用退款接口
    String agencyCode = params.getString("agencyCode");
    ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);

    try {
      result = thirdpayService.refundOrder(params);
    } catch (ServiceException e) {
      log.error("[refundOrder] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      return (ResultMap) result.withError(e.getStatus());
    } catch (Exception e) {
      log.error("[refundOrder] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      return (ResultMap) result.withError(ResultStatus.THIRD_REFUND_ERROR);
    }
    return result;
  }


  public ResultMap queryOrder(PMap params) {
    ResultMap result = ResultMap.build();

    //验证共同参数是否为空
    if (StringUtil.isEmpty(params.getString("agencyCode"), params.getString("md5securityKey"))) {
      log.error("[queryOrder] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_QUERY_PARAM_ERROR);
    }

    //根据不同支付机构调用退款接口
    String agencyCode = params.getString("agencyCode");
    ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
    try {
      result = thirdpayService.queryOrder(params);
    } catch (ServiceException e) {
      log.error("[queryOrder] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      return result.build(e.getStatus());
    } catch (Exception e) {
      log.error("[queryOrder] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      return result.build(ResultStatus.THIRD_QUERY_ERROR);
    }
    return result;
  }

  public ResultMap queryRefund(PMap params) {
    ResultMap result = ResultMap.build();

    //验证共同参数是否为空
    if (StringUtil.isEmpty(params.getString("agencyCode"), params.getString("md5securityKey"))) {
      log.error("[queryRefund] params invalid, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.THIRD_QUERY_REFUND_PARAM_ERROR);
    }

    //根据不同支付机构调用退款接口
    String agencyCode = params.getString("agencyCode");
    ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
    try {
      result = thirdpayService.queryRefundOrder(params);
    } catch (ServiceException e) {
      log.error("[queryRefund] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      return result.build(e.getStatus());
    } catch (Exception e) {
      log.error("[queryRefund] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      return result.build(ResultStatus.THIRD_QUERY_REFUND_ERROR);
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
      log.error("[getReqIDFromNotify] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      return result.build(e.getStatus());
    } catch (Exception e) {
      log.error("[getReqIDFromNotify] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      return result.build(ResultStatus.THIRD_NOTIFY_ERROR);
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
      log.error("[handleNotify] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      return result.build(e.getStatus());
    } catch (Exception e) {
      log.error("[handleNotify] failed, params={}, {}", JSONUtil.Bean2JSON(params), e.getStackTrace());
      return result.build(ResultStatus.THIRD_NOTIFY_ERROR);
    }
    return result;
  }

}
