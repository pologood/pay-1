package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayCheckFeeDiffDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PayCheckFeeDiffService {

  @Autowired
  private PayCheckFeeDiffDao payCheckFeeDiffDao;


  public void insertFeeDiff(String checkDate, String agencyCode) throws ServiceException {
    payCheckFeeDiffDao.insertFeeDiff(checkDate, agencyCode);
  }


  public void delete(String checkDate, String agencyCode) throws ServiceException {
    payCheckFeeDiffDao.delete(checkDate, agencyCode);
  }
}
