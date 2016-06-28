package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayCheckFeeResultDao;
import com.sogou.pay.service.entity.PayCheckFeeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class PayCheckFeeResultService {

  @Autowired
  private PayCheckFeeResultDao payCheckFeeResultDao;


  public void insert(String checkDate, String agencyCode) {
    payCheckFeeResultDao.insert(checkDate, agencyCode);
  }


  public void delete(String checkDate, String agencyCode) {
    payCheckFeeResultDao.delete(checkDate, agencyCode);
  }


  public void updateFeeStatus(long id, int status) {

    payCheckFeeResultDao.updateFeeStatus(id, status);
  }


  public List<PayCheckFeeResult> queryByDateAndAgency(String checkDate, String agencyCode) throws ServiceException {
    return payCheckFeeResultDao.queryByDateAndAgency(checkDate, agencyCode);
  }
}
