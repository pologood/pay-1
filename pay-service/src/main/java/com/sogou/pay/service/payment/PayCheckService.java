package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.manager.model.PayCheckUpdateModel;
import com.sogou.pay.service.dao.PayCheckDao;
import com.sogou.pay.service.entity.PayCheck;
import com.sogou.pay.thirdpay.biz.model.OutCheckRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class PayCheckService {

  @Autowired
  private PayCheckDao payCheckDao;

  public void batchInsert(List<PayCheck> payCheckList) throws ServiceException {

    payCheckDao.batchInsert(payCheckList);
  }

  public void batchUpdateStatus(List<PayCheckUpdateModel> list) throws ServiceException {

    payCheckDao.batchUpdateStatus(list);
  }

  public void deleteInfo(String checkDate, String agencyCode, String merchantNo) throws ServiceException {

    payCheckDao.deleteInfo(checkDate, agencyCode, merchantNo);
  }

  public PayCheck getByInstructIdAndCheckType(String instructId, int checkType) throws ServiceException {

    return payCheckDao.getByInstructIdAndCheckType(instructId, checkType);
  }

  public List<Map<String, Object>> queryByMerAndDateAndCheckType(
          String checkDate, String agencyCode,
          int checkType, int startRow, int batchSize) throws ServiceException {

    return payCheckDao.queryByMerAndDateAndCheckType(checkDate, agencyCode, checkType, startRow, batchSize);
  }

  public void batchUpdateFee(List<OutCheckRecord> list) throws ServiceException {

    payCheckDao.batchUpdateFee(list);
  }

}
