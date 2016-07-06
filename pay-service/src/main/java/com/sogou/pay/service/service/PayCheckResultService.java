package com.sogou.pay.service.service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayCheckResultDao;
import com.sogou.pay.service.entity.PayCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PayCheckResultService {

  @Autowired
  private PayCheckResultDao payCheckResultDao;


  public void insert(String checkDate, String agencyCode) throws ServiceException {

    payCheckResultDao.insert(checkDate, agencyCode);
  }


  public void delete(String checkDate, String agencyCode) throws ServiceException {

    payCheckResultDao.delete(checkDate, agencyCode);

  }


  public void updateStatus(long id, int status) throws ServiceException {

    payCheckResultDao.updateStatus(id, status);
  }


  public int queryCountByDateAndAgency(String checkDate, String agencyCode) throws ServiceException {
    return payCheckResultDao.queryCountByDateAndAgency(checkDate, agencyCode);
  }


  public List<PayCheckResult> queryByDateAndAgency(String checkDate, String agencyCode) throws ServiceException {

    return payCheckResultDao.queryByDateAndAgency(checkDate, agencyCode);
  }

}
