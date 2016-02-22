package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.service.dao.PayOrderDao;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.payment.PayOrderService;

import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author huangguoqing
 * @Date 2015/3/3 11:11
 * @Description: 支付单业务
 */
@Service
public class PayOrderServiceImpl implements PayOrderService {

    @Autowired
    private PayOrderDao payOrderDao;

    /**
     * 插入支付单信息
     *
     * @param payOrderInfo 支付单实体
     * @return 是否成功标识
     */
    @Override
    public int insertPayOrder(PayOrderInfo payOrderInfo) {
        return payOrderDao.insertPayOrder(payOrderInfo);
    }

    /**
     * 根据ID查询支付单信息
     *
     * @param payId 支付单ID
     * @return 支付单信息
     */
    @Override
    public PayOrderInfo selectPayOrderById(String payId) {
        return payOrderDao.selectPayOrderById(payId);
    }

    /**
     * 根据支付单流水号查询支付单信息
     *
     * @param reqId 支付单流水号
     * @return 支付单List
     */
    @Override
    public List<PayOrderInfo> selectPayOrderByPayIdList(List<PayOrderRelation> relationList) {
        return payOrderDao.selectPayOrderByPayIdList(relationList);
    }

    @Override
    public int updateAddRefundMoney(String payId, BigDecimal refundAmount, int refundFlag) throws ServiceException {
        try {
            return payOrderDao.updateAddRefundMoney(payId, refundAmount, refundFlag);
        } catch (Exception e) {
            throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
        }
    }

    /**
     * 根据ID查询支付单信息
     *
     * @param payOrderInfo 支付单信息
     * @return 支付单信息
     */
    @Override
    public void updatePayOrder(PayOrderInfo payOrderInfo) {
        payOrderDao.updatePayOrder(payOrderInfo);
    }

    /**
     * 根据支付流水单更新支付单状态
     *
     * @param payId       支付单ID
     * @param payOrderStatus 更新的状态
     * @param successTime    成功时间
     * @return 是否成功
     */
    @Override
    public void updatePayOrderByPayId(String payId, String bankCode,int payOrderStatus, Date successTime) {
        payOrderDao.updatePayOrderByPayId(payId, bankCode,payOrderStatus, successTime);
    }

    /**
     * @param orderId
     * @return 支付单信息
     * @Author huangguoqing
     * @MethodName selectPayOrderInfoByOrderId
     * @Date 2015年3月17日
     * @Description:根据订单ID查询支付单信息
     */
    @Profiled(el = true, logger = "dbTimingLogger", tag = "PayOrderService_selectPayOrderInfoByOrderId",
            timeThreshold = 100, normalAndSlowSuffixesEnabled = true)
    public PayOrderInfo selectPayOrderInfoByOrderId(String orderId,Integer appId) {
        return payOrderDao.selectPayOrderByOrderId(orderId,appId);
    }
}
