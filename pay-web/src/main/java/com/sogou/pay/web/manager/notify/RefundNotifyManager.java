package com.sogou.pay.web.manager.notify;

import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.manager.model.RefundModel;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.web.manager.SecureManager;
import com.sogou.pay.service.connect.QueueNotifyProducer;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.entity.PayResDetail;
import com.sogou.pay.service.entity.RefundInfo;
import com.sogou.pay.service.enums.RefundStatus;
import com.sogou.pay.service.payment.*;
import com.sogou.pay.web.manager.api.RefundManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


@Component
public class RefundNotifyManager {
  private static final Logger logger = LoggerFactory.getLogger(RefundNotifyManager.class);

  @Autowired
  private RefundManager refundManager;

  @Autowired
  private RefundService refundService;

  @Autowired
  private PayResDetailService payResDetailService;

  @Autowired
  private QueueNotifyProducer queueNotifyProducer;

  @Autowired
  private SecureManager secureManager;

  @Autowired
  private AppService appService;

  @Autowired
  private PayOrderService payOrderService;

  public ResultMap handleRefundNotify(PMap<String, ?> params) {
    logger.info("[handleRefundNotify] 处理退款通知开始: {}", JSONUtil.Bean2JSON(params));
    try {
      String refundId = params.getString("reqId");
      String agencyRefundId = params.getString("agencyRefundId");
      BigDecimal refundMoney = new BigDecimal(params.getString("refundMoney"));
      String refundStatus = params.getString("refundStatus");
      //查询退款订单
      RefundInfo refundInfo = refundService.selectByRefundId(refundId);
      if (refundInfo == null) {
        //退款单不存在
        logger.error("[handleRefundNotify] 查询退款订单失败, 参数: {}", JSONUtil.Bean2JSON(params));
        return ResultMap.build(ResultStatus.REFUND_NOT_EXIST);
      }
      if (refundInfo.getRefundStatus() == RefundStatus.SUCCESS.getValue()) {
        //重复通知
        logger.info("[handleRefundNotify] 重复的退款通知, 参数: {}", JSONUtil.Bean2JSON(params));
        return ResultMap.build();
      }
      //判断退款状态和退款金额，更新退款表错误信息
      if (!"SUCCESS".equals(refundStatus)) {
        //失败状态
        refundService.updateRefundFail(refundId, agencyRefundId, refundStatus, null);
        return ResultMap.build(ResultStatus.REFUND_FAILED);
      } else if (refundMoney.compareTo(refundInfo.getRefundMoney()) != 0) {
        //金额不一致
        logger.error("[handleRefundNotify] 退款金额与交易金额不一致, 参数: {}", JSONUtil.Bean2JSON(params));
        return ResultMap.build(ResultStatus.REFUND_NOT_EXIST);
      }
      //如果需要，查询支付单
      boolean isFairRefund = refundInfo.getRefundStatus() == RefundStatus.FAIR.getValue();
      PayOrderInfo payOrderInfo = null;
      if (!isFairRefund) {
        payOrderInfo = payOrderService.selectPayOrderInfoByOrderId(refundInfo.getOrderId(), refundInfo.getAppId());
        if (payOrderInfo == null) {
          logger.error("[handleRefundNotify] PayOrderInfo not exists, params={}", refundInfo.getOrderId());
          return ResultMap.build(ResultStatus.ORDER_NOT_EXIST);
        }
      }
      //查询支付流水单
      PayResDetail payResDetail = payResDetailService.selectPayResById(refundInfo.getPayDetailId());
      if (payResDetail == null) {
        logger.error("[handleRefundNotify] 回调流水不存在, payDetailId={}", refundInfo.getPayDetailId());
        return ResultMap.build(ResultStatus.RES_DETAIL_NOT_EXIST);
      }
      //退款成功，更新支付单退款金额、退款单退款成功状态
      App app = new App();
      app.setAppId(refundInfo.getAppId());
      RefundModel model = new RefundModel();
      model.setApp(app);
      model.setRefundAmount(refundInfo.getRefundMoney());
      ResultMap result = refundManager.completeRefund(model, payResDetail, payOrderInfo,
              agencyRefundId, refundId, !isFairRefund);
      if (!Result.isSuccess(result) || isFairRefund) {
        return result;
      }
      //异步通知业务线
      logger.info("[handleRefundNotify] 异步通知业务线开始: {}", JSONUtil.Bean2JSON(refundInfo));
      notifyApp(refundInfo);
      logger.info("[handleRefundNotify] 异步通知业务线结束");
      return result;
    } catch (Exception e) {
      logger.error("[handleRefundNotify] 退款通知错误, 参数: {}", JSONUtil.Bean2JSON(params), e);
      return ResultMap.build(ResultStatus.HANDLE_THIRD_NOTIFY_ERROR);
    }
  }

  /**
   * 异步通知业务线
   */
  public void notifyApp(RefundInfo refundInfo) {
    App app = appService.selectApp(refundInfo.getAppId());
    if (app == null) {
      logger.error("[asyncNotifyApp] app not exists, appId={}", refundInfo.getAppId());
      return;
    }
    PMap<String, String> map = new PMap<>();
    map.put("isSuccess", "T");
    map.put("orderId", refundInfo.getOrderId());
    map.put("payId", refundInfo.getPayId());
    map.put("payAmount", String.valueOf(refundInfo.getOrderMoney().doubleValue()));
    map.put("refundAmount", String.valueOf(refundInfo.getRefundMoney().doubleValue()));
    map.put("refundSuccessTime", DateUtil.format(new Date(), DateUtil.DATE_FORMAT_SECOND_SHORT));
    map.put("appId", String.valueOf(refundInfo.getAppId()));
    map.put("refundStatus", "SUCCESS");
    map.put("signType", "0");
    ResultMap result = (ResultMap) secureManager.doAppSign(map, null, app.getSignKey());
    Map resultMap = (Map) result.getReturnValue();
    resultMap.put("appBgUrl", refundInfo.getAppBgUrl());
    queueNotifyProducer.sendRefundMessage(resultMap);
  }

}
