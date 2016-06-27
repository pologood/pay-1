package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.RefundInfo;

import java.util.Date;
import java.util.List;

/**
 * User: hujunfei
 * Date: 2015-03-03 18:49
 */
public interface RefundService {

  /**
   * 插入退款信息表
   *
   * @param refundInfo 退款信息
   * @return 插入成功记录数
   * @throws ServiceException
   */
  public int insertRefundInfo(RefundInfo refundInfo) throws ServiceException;

  /**
   * 查询退款信息
   *
   * @param refundId 支付中心产生的退款ID
   * @return 查询结果
   * @throws ServiceException
   */
  public RefundInfo selectByRefundId(String refundId) throws ServiceException;

  /**
   * 查询退款信息
   *
   * @param payId 支付中心产生的支付单ID
   * @return 查询结果
   * @throws ServiceException
   */
  public List<RefundInfo> selectByPayId(String payId) throws ServiceException;

  /**
   * 根据支付单和退款状态查询退款单
   *
   * @param payId        支付单号
   * @param refundStatus 退款状态
   * @return 查询记录集合
   * @throws ServiceException
   */
  public List<RefundInfo> selectByPayIdAndRefundStatus(String payId, int refundStatus) throws ServiceException;

  /**
   * 修改退款状态为成功，更新完成时间
   *
   * @param refundId 退款单号
   * @param resTime  完成时间
   * @return 修改记录数
   * @throws ServiceException
   */
  public int updateRefundSuccess(String refundId, String agencyRefundId, Date resTime) throws ServiceException;

  /**
   * 修改退款状态为失败，更新（第三方支付机构）错误码和错误信息
   *
   * @param refundId  退款单号
   * @param errorCode 第三方错误码
   * @param errorMsg 第三方错误信息
   * @return 修改记录数
   * @throws ServiceException
   */
  public int updateRefundFail(String refundId, String agencyRefundId, String errorCode, String errorMsg) throws ServiceException;

  /**
   * 根据订单ID查询退款单
   *
   * @param orderId 支付单号
   * @return 查询记录集合
   * @throws ServiceException
   */
  public List<RefundInfo> selectRefundByOrderId(String orderId) throws ServiceException;
}
