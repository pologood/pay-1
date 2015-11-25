package com.sogou.pay.thirdpay.biz;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.PMap;

;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:14
 */
public interface AliPayService {

    /**
     * 1.1支付宝账户支付
     */
    public ResultMap AccountPreparePayInfo(PMap params) throws ServiceException;

    /**
     * 1.2.支付宝网关支付
     */
    public ResultMap GatwayPreparePayInfo(PMap params) throws ServiceException;

    /**
     * 1.3.支付宝扫码支付
     */
    public ResultMap SweepYardsPreparePayInfo(PMap params) throws ServiceException;

    /**
     * 1.4.支付宝客户端支付
     */
    public ResultMap ClientPreparePayInfo(PMap params) throws ServiceException;

    /**
     * 1.5.支付宝WAP支付
     */
    public ResultMap WapPreparePayInfo(PMap params) throws ServiceException;

    /**
     * 支付宝查询订单信息
     */
    public ResultMap queryOrderInfo(PMap params) throws ServiceException;

    /**
     * 支付宝订单退款
     */
    public ResultMap refundOrderInfo(PMap params) throws ServiceException;

    /**
     * 支付宝查询订单退款信息
     */
    public ResultMap queryRefundInfo(PMap params) throws ServiceException;

}
