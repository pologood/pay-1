package com.sogou.pay.service.service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.service.dao.PayCheckDayLogDao;
import com.sogou.pay.service.entity.PayCheckDayLog;
import com.sogou.pay.service.enums.CheckLogStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayCheckDayLogService {

  private static final Logger logger = LoggerFactory.getLogger(PayCheckDayLogService.class);

  @Autowired
  private PayCheckDayLogDao payCheckDayLogDao;


  public void insert(PayCheckDayLog payCheckDayLog) throws ServiceException {
    payCheckDayLogDao.insert(payCheckDayLog);
  }

  public boolean isSuccessDownload(String checkDate, String agencyCode) throws ServiceException {

    PayCheckDayLog payClearDayLogPo = null;
    try {
      payClearDayLogPo = payCheckDayLogDao
              .getByCheckDateAndAgency(agencyCode, checkDate);
    } catch (Exception e) {
      throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
    }
    if (payClearDayLogPo == null) {
      logger.warn(String
              .format("payCheckDayLog record is not exist. " +
                              "checkDate: %s| agencyCode: %s",
                      checkDate, agencyCode));
      return false;
    }
    int status = payClearDayLogPo.getStatus();
    // 如果状态不是下载完成状态，直接返回
    if (status != CheckLogStatus.SUCCESS.value()) {
      logger.warn(String
              .format("payCheckDayLog status is %s, it's not a valid status." +
                              "checkDate: %s| agencyCode: %s| status: %s",
                      checkDate, agencyCode, status,
                      checkDate));
      return false;
    }
    return true;
  }


  public boolean isFailDownload(String checkDate, String agencyCode) throws ServiceException {
    return !isSuccessDownload(checkDate, agencyCode);
  }


  public PayCheckDayLog getByCheckDateAndAgency(String checkDate, String agencyCode) throws ServiceException {
    return payCheckDayLogDao.getByCheckDateAndAgency(checkDate, agencyCode);

  }


  public void updateStatus(long id, int status, int version, String remark) throws ServiceException {
    payCheckDayLogDao.updateStatus(id, status, version, remark);
  }

}
