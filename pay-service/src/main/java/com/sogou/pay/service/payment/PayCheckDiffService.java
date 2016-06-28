package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayCheckDiffDao;
import com.sogou.pay.service.entity.PayCheckDiff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PayCheckDiffService {

  @Autowired
  private PayCheckDiffDao payCheckDiffDao;


  public void insertAmtDiff(String checkDate, String agencyCode) throws ServiceException {
    payCheckDiffDao.insertAmtDiff(checkDate, agencyCode);
  }


  public void insertOutMoreDiff(String checkDate, String agencyCode) throws ServiceException {
    payCheckDiffDao.insertOutMoreDiff(checkDate, agencyCode);

  }


  public void insertOutLessDiff(String checkDate, String agencyCode) throws ServiceException {
    payCheckDiffDao.insertOutLessDiff(checkDate, agencyCode);
  }


  public void delete(String checkDate, String agencyCode) throws ServiceException {
    payCheckDiffDao.delete(checkDate, agencyCode);
  }


  public int selectUnResolvedCount() throws ServiceException {
    return payCheckDiffDao.selectUnResolvedCount();
  }


  public List<PayCheckDiff> selectUnResolvedList() throws ServiceException {
    return payCheckDiffDao.selectUnResolvedList();
  }


  public void updateStatus(Long id, int status, String remark) throws ServiceException {
    payCheckDiffDao.updateStatus(id, status, remark);
  }
}
