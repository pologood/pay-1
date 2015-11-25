package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayCheckFeeDiffDao;
import com.sogou.pay.service.payment.PayCheckFeeDiffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by qibaichao on 2015/3/23.
 */
@Service
public class PayCheckFeeDiffServiceImpl implements PayCheckFeeDiffService {

    @Autowired
    private PayCheckFeeDiffDao payCheckFeeDiffDao;

    @Override
    public void insertFeeDiff(String checkDate, String agencyCode) throws ServiceException {
        payCheckFeeDiffDao.insertFeeDiff(checkDate,  agencyCode);
    }

    @Override
    public void delete(String checkDate, String agencyCode) throws ServiceException {
        payCheckFeeDiffDao.delete(checkDate,  agencyCode);
    }
}
