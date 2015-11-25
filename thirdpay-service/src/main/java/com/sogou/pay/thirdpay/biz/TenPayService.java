package com.sogou.pay.thirdpay.biz;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.PMap;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:14
 */
public interface TenPayService {

    /**
     * 1.1财付通账户支付
     */
    public ResultMap AccountPreparePayInfo(PMap params) throws ServiceException;

    /**
     * 1.2.财付通网关支付
     */
    public ResultMap GatwayYPreparePayInfo(PMap params) throws ServiceException;

    /**
     * 1.3.财付通客户端支付
     */
    public ResultMap ClientPreparePayInfo(PMap params) throws ServiceException;

    /**
     * 财付通查询订单信息
     */
    public ResultMap queryOrderInfo(PMap params) throws ServiceException;

    /**
     * 财付通订单退款
     */
    public ResultMap refundOrderInfo(PMap params) throws ServiceException;

    /**
     * 财付通查询订单退款信息
     */
    public ResultMap queryRefundInfo(PMap params) throws ServiceException;
}
