package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayCheckFeeResultDao;
import com.sogou.pay.service.entity.PayCheckFeeResult;
import com.sogou.pay.service.payment.PayCheckFeeResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by qibaichao on 2015/3/20.
 */
@Service
public class PayCheckFeeResultServiceImpl implements PayCheckFeeResultService {

    @Autowired
    private PayCheckFeeResultDao payCheckFeeResultDao;

    @Override
    public void insert(String checkDate, String agencyCode) {
        payCheckFeeResultDao.insert(checkDate, agencyCode);
    }

    @Override
    public void delete(String checkDate, String agencyCode) {
        payCheckFeeResultDao.delete(checkDate, agencyCode);
    }

    @Override
    public void updateFeeStatus(long id,  int status) {

        payCheckFeeResultDao.updateFeeStatus(id,  status);
    }

    @Override
    public List<PayCheckFeeResult> queryByDateAndAgency(String checkDate, String agencyCode) throws ServiceException {
        return payCheckFeeResultDao.queryByDateAndAgency(checkDate, agencyCode);
    }
}
