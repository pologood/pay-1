package com.sogou.pay.service.payment;

import java.util.List;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.entity.PayReqDetail;

/**
 * @User: huangguoqing
 * @Date: 2015/03/03
 * @Description: 支付流水服务
 */
public interface PayReqDetailService {
    /**
     * 插入支付流水信息
     * @param payReqDetail 支付流水实体
     * @return 返回值
     */
    public int insertPayReqDetail(PayReqDetail payReqDetail) throws ServiceException;

    /**
     * 根据ID查询支付单流水信息
     * @param payReqId 支付流水ID
     * @return 返回值
     */
    public PayReqDetail selectPayReqDetailById(String payReqId);

    /**
     * @Author  huangguoqing 
     * @MethodName  selectPayReqByReqIdList 
     * @param orderId
     * @param appId
     * @return 响应流水信息
     * @throws ServiceException 
     * @Date    2015年4月16日
     * @Description:根据商户订单ID查询响应流水信息
     */
    public List<PayReqDetail> selectPayReqByReqIdList(List<PayOrderRelation> relationList) throws ServiceException;
}
