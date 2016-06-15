package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.manager.model.PayCheckUpdateModel;
import com.sogou.pay.service.dao.PayCheckWaitingDao;
import com.sogou.pay.service.entity.PayCheckWaiting;
import com.sogou.pay.service.payment.PayCheckWaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName PayCheckWaitingServiceImpl
 * @Date 2015年3月2日
 * @Description:
 */
@Service
public class PayCheckWaitingServiceImpl implements PayCheckWaitingService {

    @Autowired
    private PayCheckWaitingDao payCheckWaitingDao;

    @Override
    public int insert(PayCheckWaiting payCheckWaiting) {
        return payCheckWaitingDao.insert(payCheckWaiting);
    }

    @Override
    public PayCheckWaiting getByInstructId(String instructId) throws ServiceException {

        return payCheckWaitingDao.getByInstructId(instructId);
    }

    @Override
    public void batchUpdateStatus(List<PayCheckUpdateModel> list) throws ServiceException {

        payCheckWaitingDao.batchUpdateStatus(list);
    }

    @Override
    public Map<String, Object> sumAmtAndNum(
            String checkDate, String agencyCode, int bizCode) throws ServiceException {

        return payCheckWaitingDao.sumAmtAndNum(checkDate, agencyCode, bizCode);
    }

    @Override
    public Map<String, Object> sumFeeAmtAndNum(String checkDate, String agencyCode, int bizCode) throws ServiceException {
        return payCheckWaitingDao.sumFeeAmtAndNum(checkDate, agencyCode, bizCode);
    }

}
