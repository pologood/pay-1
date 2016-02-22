package com.sogou.pay.thirdpay.biz;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/06/16 16:14
 */
public interface BillPayService {

    /**
     * 1.快钱账户支付
     */
    public ResultMap preparePayInfo(PMap params) throws ServiceException;

    /**
     * 2.快钱查询订单信息
     */
    public ResultMap queryOrderInfo(PMap params) throws ServiceException;

    /**
     * 3.快钱订单退款
     */
    public ResultMap refundOrderInfo(PMap params) throws ServiceException;

    /**
     * 4.快钱查询订单退款信息
     */
    public ResultMap queryRefundInfo(PMap params) throws ServiceException;
}
