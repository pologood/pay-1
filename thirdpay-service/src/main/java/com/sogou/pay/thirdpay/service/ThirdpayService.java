package com.sogou.pay.thirdpay.service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;

/**
 * Created by xiepeidong on 2016/1/14.
 */
public interface ThirdpayService {
    /**
     * (支付宝)账户支付
     */
    public ResultMap preparePayInfoAccount(PMap params) throws ServiceException;

    /**
     * (支付宝)网关支付
     */
    public ResultMap preparePayInfoGatway(PMap params) throws ServiceException;

    /**
     * (支付宝)扫码支付
     */
    public ResultMap preparePayInfoQRCode(PMap params) throws ServiceException;

    /**
     * (支付宝)客户端支付
     */
    public ResultMap preparePayInfoSDK(PMap params) throws ServiceException;

    /**
     * (支付宝)WAP支付
     */
    public ResultMap preparePayInfoWap(PMap params) throws ServiceException;

    /**
     * (支付宝)查询订单信息
     */
    public ResultMap queryOrder(PMap params) throws ServiceException;

    /**
     * (支付宝)订单退款
     */
    public ResultMap refundOrder(PMap params) throws ServiceException;

    /**
     * (支付宝)查询订单退款信息
     */
    public ResultMap queryRefundOrder(PMap params) throws ServiceException;

    /**
     * (支付宝)下载对账单
     */
    public ResultMap downloadOrder(PMap params) throws ServiceException;

}
