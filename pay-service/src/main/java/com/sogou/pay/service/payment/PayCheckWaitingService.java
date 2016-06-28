package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.manager.model.PayCheckUpdateModel;
import com.sogou.pay.service.dao.PayCheckWaitingDao;
import com.sogou.pay.service.entity.PayCheckWaiting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class PayCheckWaitingService {

  @Autowired
  private PayCheckWaitingDao payCheckWaitingDao;

  public int insert(PayCheckWaiting payCheckWaiting) {
    return payCheckWaitingDao.insert(payCheckWaiting);
  }

  public PayCheckWaiting getByInstructId(String instructId) throws ServiceException {

    return payCheckWaitingDao.getByInstructId(instructId);
  }

  public void batchUpdateStatus(List<PayCheckUpdateModel> list) throws ServiceException {

    payCheckWaitingDao.batchUpdateStatus(list);
  }

  public Map<String, Object> sumAmtAndNum(
          String checkDate, String agencyCode, int bizCode) throws ServiceException {

    return payCheckWaitingDao.sumAmtAndNum(checkDate, agencyCode, bizCode);
  }

  public Map<String, Object> sumFeeAmtAndNum(String checkDate, String agencyCode, int bizCode) throws ServiceException {
    return payCheckWaitingDao.sumFeeAmtAndNum(checkDate, agencyCode, bizCode);
  }

}
