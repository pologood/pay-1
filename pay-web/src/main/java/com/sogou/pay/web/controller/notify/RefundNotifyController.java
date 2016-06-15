package com.sogou.pay.web.controller.notify;

import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.web.manager.notify.RefundNotifyManager;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.web.portal.PayPortal;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by xiepeidong on 2016/3/4.
 */

@Controller
@RequestMapping("/notify/refund")
public class RefundNotifyController {

  private static final Logger log = LoggerFactory.getLogger(RefundNotifyController.class);

  @Autowired
  private PayPortal payPortal;

  @Autowired
  private RefundNotifyManager refundNotifyManager;

  @Autowired
  private PayAgencyMerchantService payAgencyMerchantService;

  /**
   * 支付宝、财付通等退款异步回调入口
   */
  @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/refund",
          timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
  @RequestMapping("/{agencyCode}/{merchantId}")
  @ResponseBody
  public String handleNotifyRefund(@PathVariable("agencyCode") String agencyCode,
                                   @PathVariable("merchantId") String merchantId,
                                   @RequestParam Map params) {
    log.info("[handleNotifyRefund] 处理第三方退款异步通知, 参数: agencyCode={}, merchantId={}, {}",
            agencyCode, merchantId, JSONUtil.Bean2JSON(params));

    String notifyType = "REFUND";

    PMap requestPMap = new PMap();
    requestPMap.put("agencyCode", agencyCode.toUpperCase());
    requestPMap.put("notifyType", notifyType.toUpperCase());
    requestPMap.put("data", new PMap(params));

    //验签
    PayAgencyMerchant payAgencyMerchant = payAgencyMerchantService.selectPayAgencyMerchantById(Integer.parseInt(merchantId));
    if (payAgencyMerchant == null) {
      log.error("[handleNotifyAsync] 查询商户信息失败, merchantId={}", merchantId);
      return "success";
    }
    String md5securityKey = payAgencyMerchant.getEncryptKey();
    String publicCertFilePath = payAgencyMerchant.getPubKeypath();
    requestPMap.put("md5securityKey", md5securityKey);
    requestPMap.put("publicCertFilePath", publicCertFilePath);

    ResultMap result = payPortal.handleNotify(requestPMap);
    if (result.getStatus() != ResultStatus.SUCCESS) {
      log.error("[handleNotifyAsync] 验证签名、提取参数失败, 参数: {}", requestPMap);
      return "success";
    }

    //继续退款后续逻辑
    requestPMap = result.getData();
    result = refundNotifyManager.handleRefundNotify(requestPMap);
    if (!Result.isSuccess(result)) {
      log.error("[handleNotifyAsync] 退款后续逻辑执行失败, 参数: {}, 返回: {}", JSONUtil.Bean2JSON(requestPMap),
              JSONUtil.Bean2JSON(result));
    }
    return "success";
  }
}
