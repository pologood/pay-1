package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayCheckDiffDao;
import com.sogou.pay.service.entity.PayCheckDiff;
import com.sogou.pay.service.payment.PayCheckDiffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qibaichao on 2015/3/23.
 */
@Service
public class PayCheckDiffServiceImpl implements PayCheckDiffService {

    @Autowired
    private PayCheckDiffDao payCheckDiffDao;

    @Override
    public void insertAmtDiff(String checkDate, String agencyCode) throws ServiceException {
        payCheckDiffDao.insertAmtDiff(checkDate, agencyCode);
    }

    @Override
    public void insertOutMoreDiff(String checkDate, String agencyCode) throws ServiceException {
        payCheckDiffDao.insertOutMoreDiff(checkDate, agencyCode);

    }

    @Override
    public void insertOutLessDiff(String checkDate, String agencyCode) throws ServiceException {
        payCheckDiffDao.insertOutLessDiff(checkDate, agencyCode);
    }

    @Override
    public void delete(String checkDate, String agencyCode) throws ServiceException {
        payCheckDiffDao.delete(checkDate, agencyCode);
    }

    @Override
    public int selectUnResolvedCount() throws ServiceException {
        return payCheckDiffDao.selectUnResolvedCount();
    }

    @Override
    public List<PayCheckDiff> selectUnResolvedList() throws ServiceException {
        return payCheckDiffDao.selectUnResolvedList();
    }

    @Override
    public void updateStatus(Long id, int status, String remark) throws ServiceException {
         payCheckDiffDao.updateStatus(id,status,remark);
    }
}
