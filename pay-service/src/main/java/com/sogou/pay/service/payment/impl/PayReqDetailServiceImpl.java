package com.sogou.pay.service.payment.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.service.dao.PayReqDetailDao;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.entity.PayReqDetail;
import com.sogou.pay.service.payment.PayReqDetailService;

/**
 * @Author huangguoqing
 * @Date 2015/3/3 11:11
 * @Description: 支付流水业务
 */
@Service
public class PayReqDetailServiceImpl implements PayReqDetailService {

    @Autowired
    private PayReqDetailDao payReqDetailDao;

    /**
     * 插入支付单信息
     * @param payReqDetail 支付单实体
     * @return 是否成功标识
     */
    @Override
    public int insertPayReqDetail(PayReqDetail payReqDetail) throws ServiceException{
        try{
            return payReqDetailDao.insertPayReqDetail(payReqDetail);
        } catch (Exception e){
            throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
        }
    }

    /**
     * 根据ID查询支付单流水信息
     * @param payReqId 支付流水ID
     * @return 返回值
     */
    public PayReqDetail selectPayReqDetailById(String payReqId){
        return payReqDetailDao.selectPayReqDetailById(payReqId);
    }
    
    @Override
    public List<PayReqDetail> selectPayReqByReqIdList(List<PayOrderRelation> relationList)
            throws ServiceException {
        return payReqDetailDao.selectPayReqByReqIdList(relationList);
    }
}
