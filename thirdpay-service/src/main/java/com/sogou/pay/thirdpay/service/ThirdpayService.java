package com.sogou.pay.thirdpay.service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;

/**
 * Created by xiepeidong on 2016/1/14.
 */
public interface ThirdpayService {

  /**
   * (支付宝等)账户支付
   */
  default public ResultMap<?> preparePayInfoAccount(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)网关支付
   */
  default public ResultMap<?> preparePayInfoGatway(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)扫码支付
   */
  default public ResultMap<?> preparePayInfoQRCode(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)客户端支付
   */
  default public ResultMap<?> preparePayInfoSDK(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)WAP支付
   */
  default public ResultMap<?> preparePayInfoWap(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)查询订单信息
   */
  default public ResultMap<?> queryOrder(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)订单退款
   */
  default public ResultMap<?> refundOrder(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)查询订单退款信息
   */
  default public ResultMap<?> queryRefundOrder(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)下载对账单
   */
  default public ResultMap<?> downloadOrder(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)批量付款
   */
  default public ResultMap<?> prepareTransferInfo(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)查询批量付款结果
   */
  default public ResultMap<?> queryTransfer(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)查询付款退票信息
   */
  default public ResultMap<?> queryTransferRefund(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  /**
   * (支付宝等)处理支付、退款、转账的同步、异步通知
   */
  default public ResultMap<?> getReqIDFromNotifyWebSync(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> getReqIDFromNotifyWebAsync(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> getReqIDFromNotifyWapSync(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> getReqIDFromNotifyWapAsync(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> getReqIDFromNotifySDKAsync(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> getReqIDFromNotifyRefund(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> getReqIDFromNotifyTransfer(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> handleNotifyWebSync(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> handleNotifyWebAsync(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> handleNotifyWapSync(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> handleNotifyWapAsync(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> handleNotifySDKAsync(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> handleNotifyRefund(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  default public ResultMap<?> handleNotifyTransfer(PMap<String, ?> params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

}
