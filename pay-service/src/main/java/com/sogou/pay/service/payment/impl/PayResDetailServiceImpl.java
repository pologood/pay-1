package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.service.dao.PayReqDetailDao;
import com.sogou.pay.service.dao.PayResDetailDao;
import com.sogou.pay.service.entity.PayResDetail;
import com.sogou.pay.service.payment.PayResDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Author liwei
 * @Date 2015/3/6 19:11
 * @Description: 支付回调流水业务
 */
@Service
public class PayResDetailServiceImpl implements PayResDetailService {

    @Autowired
    private PayResDetailDao payResDetailDao;


    @Override
    public PayResDetail selectByAgencyOrderId(String agencyOrderId) throws ServiceException {
        return payResDetailDao.selectByAgencyOrderId(agencyOrderId);
    }

    @Override
    public int insertPayResDetail(PayResDetail payResDetail) throws ServiceException {
        return payResDetailDao.insertPayResDetail(payResDetail);
    }

    @Override
    public PayResDetail selectPayResById(String payResId) throws ServiceException {
        return payResDetailDao.selectPayResById(payResId);
    }

    @Override
    public int updatePayResPayfeeById(BigDecimal payFee,BigDecimal feeRate, String payResId) throws ServiceException {
        return payResDetailDao.updatePayResPayfeeById(payFee,feeRate, payResId);

    }
}
