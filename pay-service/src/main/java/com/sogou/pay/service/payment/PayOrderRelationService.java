package com.sogou.pay.service.payment;

import java.util.List;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayOrderRelation;

/**
 * @User: huangguoqing
 * @Date: 2015/03/03
 * @Description: 支付单与支付流水关联业务
 */
public interface PayOrderRelationService {
    /**
     * 插入支付单与支付流水关联实体信息
     *
     * @param payOrderRelation 支付单与支付流水关联实体
     * @return 返回值
     */
    public int insertPayOrderRelation(PayOrderRelation payOrderRelation) throws ServiceException;

    /**
     * 查询支付单与支付流水关联信息
     *
     * @param payOrderRelation 条件
     * @return 关联信息List
     */
    public List<PayOrderRelation> selectPayOrderRelation(PayOrderRelation payOrderRelation);

    /**
     * 根据支付流水号查询支付单号
     *
     * @param payDetailId
     * @return
     */
    public String selectPayOrderId(String payDetailId);
    
    /**
     * @Author	huangguoqing 
     * @MethodName	updatePayOrderRelationByReqId
     * @param status 
     * @param payDetailId
     * @return 更新条数
     * @Date	2015年4月20日
     * @Description:根据支付单流水更新关联表
     */
    public int updatePayOrderRelation(int status,String payDetailId) throws ServiceException;
}
