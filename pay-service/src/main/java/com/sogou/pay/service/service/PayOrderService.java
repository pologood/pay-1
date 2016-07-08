package com.sogou.pay.service.service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.service.dao.PayOrderDao;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayOrderRelation;

import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class PayOrderService {

  @Autowired
  private PayOrderDao payOrderDao;

  /**
   * 插入支付单信息
   */

  public int insertPayOrder(PayOrderInfo payOrderInfo) {
    return payOrderDao.insertPayOrder(payOrderInfo);
  }

  /**
   * 根据ID查询支付单信息
   */

  public PayOrderInfo selectPayOrderById(String payId) {
    return payOrderDao.selectPayOrderById(payId);
  }

  /**
   * 根据支付单流水号查询支付单信息
   */

  public List<PayOrderInfo> selectPayOrderByPayIdList(List<PayOrderRelation> relationList) {
    return payOrderDao.selectPayOrderByPayIdList(relationList);
  }


  public int updateAddRefundMoney(String payId, BigDecimal refundAmount, int refundFlag) throws ServiceException {
    try {
      return payOrderDao.updateAddRefundMoney(payId, refundAmount, refundFlag);
    } catch (Exception e) {
      throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
    }
  }

  /**
   * 根据ID查询支付单信息
   */

  public void updatePayOrder(PayOrderInfo payOrderInfo) {
    payOrderDao.updatePayOrder(payOrderInfo);
  }

  /**
   * 根据支付流水单更新支付单状态
   */

  public void updatePayOrderByPayId(String payId, String channelCode, int payStatus, Date paySuccessTime) {
    payOrderDao.updatePayOrderByPayId(payId, channelCode, payStatus, paySuccessTime);
  }

  /**根据订单ID查询支付单信息
   */
  @Profiled(el = true, logger = "dbTimingLogger", tag = "PayOrderService_selectPayOrderInfoByOrderId",
          timeThreshold = 100, normalAndSlowSuffixesEnabled = true)
  public PayOrderInfo selectPayOrderInfoByOrderId(String orderId, Integer appId) {
    return payOrderDao.selectPayOrderByOrderId(orderId, appId);
  }
}
