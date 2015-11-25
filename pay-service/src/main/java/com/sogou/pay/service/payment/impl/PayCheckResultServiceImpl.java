package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayCheckResultDao;
import com.sogou.pay.service.entity.PayCheckResult;
import com.sogou.pay.service.payment.PayCheckResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class PayCheckResultServiceImpl implements PayCheckResultService {

    @Autowired
    private PayCheckResultDao payCheckResultDao;

    @Override
    public void insert(String checkDate, String agencyCode) throws ServiceException {

        payCheckResultDao.insert(checkDate, agencyCode);
    }

    @Override
    public void delete(String checkDate, String agencyCode) throws ServiceException {

        payCheckResultDao.delete(checkDate, agencyCode);

    }

    @Override
    public void updateStatus(long id, int status) throws ServiceException {

        payCheckResultDao.updateStatus(id, status);
    }

    @Override
    public int queryCountByDateAndAgency(String checkDate, String agencyCode) throws ServiceException {
        return payCheckResultDao.queryCountByDateAndAgency(checkDate, agencyCode);
    }

    @Override
    public List<PayCheckResult> queryByDateAndAgency(String checkDate, String agencyCode) throws ServiceException {

        return payCheckResultDao.queryByDateAndAgency(checkDate, agencyCode);
    }

}
