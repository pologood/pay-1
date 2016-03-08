package com.sogou.pay.web.controller.notify;

import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.manager.notify.RefundNotifyManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.thirdpay.api.PayPortal;
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
 * User: hujunfei Date: 2015-03-02 18:39 支付宝异步回调处理
 */
@Controller
@RequestMapping("/notify/refund")
public class RefundNotifyController {

    private static final Logger log = LoggerFactory.getLogger(RefundNotifyController.class);

    @Autowired
    private PayPortal payPortal;

    @Autowired
    private SecureManager secureManager;
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
        log.info("[handleNotifyRefund] 处理第三方退款异步通知, " + JSONUtil.Bean2JSON(params));

        String notifyType = "REFUND";

        PMap requestPMap = new PMap();
        requestPMap.put("agencyCode", agencyCode.toUpperCase());
        requestPMap.put("notifyType", notifyType.toUpperCase());
        requestPMap.put("data", new PMap(params));


        // 2.验证签名
        PayAgencyMerchant payAgencyMerchant = payAgencyMerchantService.selectPayAgencyMerchantById(Integer.parseInt(merchantId));
        if (payAgencyMerchant == null) {
            log.error("[handleNotifyAsync] 查询商户信息失败, merchantId=" + merchantId);
            return "success";
        }
        //获取签名key
        String md5securityKey = payAgencyMerchant.getEncryptKey();
        String publicCertFilePath = payAgencyMerchant.getPubKeypath();
        requestPMap.put("md5securityKey", md5securityKey);
        requestPMap.put("publicCertFilePath", publicCertFilePath);

        //验证签名，提取参数
        ResultMap result = payPortal.handleNotify(requestPMap);
        if (result.getStatus() != ResultStatus.SUCCESS) {
            log.error("[handleNotifyAsync] 验证签名、提取参数失败, 参数:" + requestPMap);
            return "success";
        }

        // 3.执行退款回调逻辑
        requestPMap = result.getData();
        result =
                (ResultMap) refundNotifyManager.handleRefundNotify(requestPMap);
        if (!Result.isSuccess(result)) {
            log.warn("[handleNotifyAsync] 退款回调逻辑执行失败, " + result.getMessage() + ", 参数:" + JSONUtil.Bean2JSON(requestPMap));
            return "success";
        }
        Object retValue = result.getReturnValue();
        if (retValue == null) {
            log.error("[handleNotifyAsync] 无回调数据, 参数:" + JSONUtil.Bean2JSON(params));
            return "success";
        }
        //平账退款不通知
        if (retValue.equals(9)) {
            return "success";
        }
        // 4.处理应用回调请求
        log.info("[handleNotifyAsync] 向业务线发起退款回调开始, agencyCode=" + agencyCode + ", merchantId=" + merchantId);
        Result secureResult = secureManager.appSign(retValue);
        result.withReturn(secureResult.getReturnValue());
        refundNotifyManager.notifyApp(result);
        log.info("[handleNotifyAsync] 向业务线发起退款回调结束");
      return "success";
    }
}
