package com.sogou.pay.thirdpay.biz;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.PMap;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:14
 */
public interface WechatPayService {

    /**
     * 3.1.微信扫码支付
     */
    public ResultMap SweepYardsPreparePayInfo(PMap params) throws ServiceException;

    /**
     * 3.2.微信客户端支付
     */
    public ResultMap ClientPreparePayInfo(PMap params) throws ServiceException;

    /**
     * 微信查询订单信息
     */
    public ResultMap queryOrderInfo(PMap params) throws ServiceException;

    /**
     * 微信订单退款
     */
    public ResultMap refundOrderInfo(PMap params) throws ServiceException;

    /**
     * 微信查询订单退款信息
     */
    public ResultMap queryRefundInfo(PMap params) throws ServiceException;
}
