package com.sogou.pay.service.payment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayOrderRelation;

/**
 * @User: huangguoqing
 * @Date: 2015/03/03
 * @Description: 支付单服务
 */
public interface PayOrderService {
    /**
     * 插入支付单信息
     * @param payOrderInfo 支付单实体
     * @return 返回值
     */
    public int insertPayOrder(PayOrderInfo payOrderInfo) throws ServiceException;

    /**
     * 根据ID查询支付单信息
     * @param payId 支付单ID
     * @return 支付单信息
     */
    public PayOrderInfo selectPayOrderById(String payId) throws ServiceException;

    /**
     * 根据支付单流水号查询支付单信息
     * @param reqId 支付单流水号
     * @return 支付单List
     */
    public List<PayOrderInfo> selectPayOrderByPayIdList(List<PayOrderRelation> relationList) throws ServiceException;

    /**
     * 根据ID增加退款金额
     * @param payId 支付单ID
     * @param refundAmount 退款金额
     * @return 修改成功记录数
     */
    public int updateAddRefundMoney(String payId, BigDecimal refundAmount, int refundFlag) throws ServiceException;

    /**
     * 根据ID查询支付单信息
     * @param payOrderInfo 支付单信息
     * @return 支付单信息
     */
    public void updatePayOrder(PayOrderInfo payOrderInfo) throws ServiceException;

    /**
     * 根据支付流水单批量更新支付单状态
     * @param payId 支付单ID
     * @param bankCode 银行编码
     * @param status 更新的状态
     * @param successTime 成功时间
     * @return 是否成功
     */
    public void updatePayOrderByPayId(String payId,String bankCode,int status,Date successTime) throws ServiceException;

    /**
     * @Author	huangguoqing 
     * @MethodName	selectPayOrderInfoByOrderId 
     * @param orderId
     * @param appId
     * @return 支付单信息
     * @Date	2015年3月17日
     * @Description:根据订单ID查询支付单信息
     */
    public PayOrderInfo selectPayOrderInfoByOrderId(String orderId,Integer appId) throws ServiceException;
    
}
