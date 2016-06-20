package com.sogou.pay.web.manager.notify;

import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.manager.model.RefundModel;
import com.sogou.pay.manager.model.PayNotifyModel;
import com.sogou.pay.service.enums.RefundStatus;
import com.sogou.pay.web.manager.api.PayManager;
import com.sogou.pay.web.manager.api.RefundManager;
import com.sogou.pay.web.manager.SecureManager;
import com.sogou.pay.service.connect.QueueNotifyProducer;
import com.sogou.pay.service.entity.*;
import com.sogou.pay.service.payment.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class PayNotifyManager {

  private static final Logger logger = LoggerFactory.getLogger(PayNotifyManager.class);

  @Autowired
  private PayOrderService payOrderService;

  @Autowired
  private PayReqDetailService payReqDetailService;

  @Autowired
  private AppService appService;

  @Autowired
  private QueueNotifyProducer queueNotifyProducer;

  @Autowired
  private PayOrderRelationService payOrderRelationService;

  @Autowired
  private SecureManager secureManager;

  @Autowired
  private RefundManager refundManager;

  @Autowired
  private PayManager payManager;

  /**
   * 业务上校验回调参数是否合法，包括
   * 1.验证
   * 2.业务勾兑
   * 3.通知
   */
  public ResultMap handlePayNotify(PayNotifyModel payNotifyModel) {
    ResultMap result = ResultMap.build();
    String payDetailId = payNotifyModel.getPayDetailId();
    try {
      logger.info("[handlePayNotify] 处理第三方异步通知: {}", JSONUtil.Bean2JSON(payNotifyModel));
      PayReqDetail payReqDetail = payReqDetailService.selectPayReqDetailById(payDetailId);
      if (payReqDetail == null) {
        logger.error("[handlePayNotify] 查询PayReqDetail失败: {}", JSONUtil.Bean2JSON(payNotifyModel));
        return (ResultMap) result.withError(ResultStatus.REQ_DETAIL_NOT_EXIST_ERROR);
      }
      //检验金额
      BigDecimal trueMoney = payReqDetail.getTrueMoney();
      BigDecimal trueMoney_notify = payNotifyModel.getTrueMoney();
      if (trueMoney.compareTo(trueMoney_notify) != 0) {
        logger.error("[handlePayNotify] 金额不相等, payReqId={}, PayReqDetail.trueMoney={}, payNotifyModel.trueMoney={}"
                , payReqDetail.getPayDetailId(), trueMoney, trueMoney_notify);
        return (ResultMap) result.withError(ResultStatus.REQ_DETAIL_NOT_EXIST_ERROR);
      }
      //获取支付单号
      String payOrderId = payOrderRelationService.selectPayOrderId(payDetailId);
      PayOrderInfo payOrderInfo = payOrderService.selectPayOrderById(payOrderId);
      if (payOrderInfo == null) {
        logger.error("[handlePayNotify] 查询PayOrderInfo失败: {}", JSONUtil.Bean2JSON(payNotifyModel));
        return (ResultMap) result.withError(ResultStatus.PAY_ORDER_NOT_EXIST);
      }

      payManager.completePay(payNotifyModel, payOrderInfo, payReqDetail);

      //最后,检验重复支付
      if (OrderStatus.SUCCESS.getValue() == payOrderInfo.getPayOrderStatus()) {
        //重复支付，需退款
        logger.info("[handlePayNotify] 重复支付，需退款: {}", JSONUtil.Bean2JSON(payNotifyModel));
        App app = new App();
        app.setAppId(payOrderInfo.getAppId());
        RefundModel model = new RefundModel();
        model.setApp(app);
        model.setOrderId(payOrderInfo.getOrderId());
        model.setRefundAmount(trueMoney_notify);
        model.setRefundStatus(RefundStatus.FAIR.getValue());
        refundManager.refundPay(model, payOrderInfo, payDetailId);
        return result;
      }

      //异步通知业务线
      logger.info("[handlePayNotify] 异步通知业务线开始: {}", JSONUtil.Bean2JSON(payNotifyModel));
      asyncNotifyApp(payOrderInfo, payNotifyModel);
      logger.info("[handlePayNotify] 异步通知业务线结束");

    } catch (DuplicateKeyException e) {
      //支付成功，重复通知，丢弃该通知
      logger.error("[handlePayNotify] 重复的第三方异步通知, 丢弃: {}", JSONUtil.Bean2JSON(payNotifyModel));
      result.withError(ResultStatus.RES_DETAIL_ALREADY_EXIST);
    } catch (Exception e) {
      logger.error("[handlePayNotify] 处理第三方异步通知失败: {}, {}", JSONUtil.Bean2JSON(payNotifyModel), e);
      result.withError(ResultStatus.PAY_NOTIFY_ERROR);
    }
    return result;
  }

  /**
   * 异步通知业务线
   */
  public void asyncNotifyApp(PayOrderInfo payOrderInfo, PayNotifyModel patyNotifyModel) {
    App app = appService.selectApp(payOrderInfo.getAppId());
    if (app == null) {
      logger.error("[asyncNotifyApp] app not exists, appId={}", payOrderInfo.getAppId());
      return;
    }
    PMap<String, String> map = new PMap<String, String>();
    map.put("isSuccess", "T");
    map.put("appId", payOrderInfo.getAppId().toString());
    map.put("signType", "0");
    map.put("orderId", payOrderInfo.getOrderId());
    map.put("payId", payOrderInfo.getPayId());
    map.put("orderMoney", payOrderInfo.getOrderMoney().toString());
    map.put("tradeStatus", "SUCCESS");
    map.put("successTime", DateUtil.format(patyNotifyModel.getAgencyPayTime(), DateUtil.DATE_FORMAT_SECOND_SHORT));
    ResultMap result = (ResultMap) secureManager.doAppSign(map, null, app.getSignKey());
    Map resultMap = (Map) result.getReturnValue();
    resultMap.put("appBgUrl", payOrderInfo.getAppBgUrl());
    queueNotifyProducer.sendPayMessage(resultMap);
  }

  /**
   * 获得同步回调参数
   */
  public ResultMap<Map> syncNotifyApp(PayOrderInfo payOrderInfo) {
    App app = appService.selectApp(payOrderInfo.getAppId());
    if (app == null) {
      logger.error("[syncNotifyApp] app not exists, appId={}", payOrderInfo.getAppId());
      return ResultMap.build(ResultStatus.PAY_APP_NOT_EXIST);
    }
    Map<String, String> notifyMap = new HashMap<>();
    notifyMap.put("isSuccess", "T");
    notifyMap.put("appId", payOrderInfo.getAppId().toString());
    notifyMap.put("signType", "0");
    notifyMap.put("orderId", payOrderInfo.getOrderId());
    notifyMap.put("payId", payOrderInfo.getPayId());
    notifyMap.put("payChannelCode", payOrderInfo.getChannelCode());
    notifyMap.put("successTime", DateUtil.format(payOrderInfo.getPaySuccessTime(), DateUtil.DATE_FORMAT_SECOND_SHORT));
    Result result = secureManager.doAppSign(notifyMap, null, app.getSignKey());
    if (!Result.isSuccess(result)) {
      logger.error("[syncNotifyApp] 获取同步通知参数失败, {}", result.getStatus().getMessage());
      return ResultMap.build(result.getStatus());
    }
    ResultMap resultMap = ResultMap.build();
    resultMap.withReturn(result.getReturnValue());
    return resultMap;
  }
}
