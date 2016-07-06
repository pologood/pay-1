package com.sogou.pay.service.service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayTransferLogDao;
import com.sogou.pay.service.entity.PayTransferLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PayTransferLogService {

  @Autowired
  private PayTransferLogDao payTransferLogDao;


  public void insert(PayTransferLog payTransferLog) throws ServiceException {
    payTransferLogDao.insert(payTransferLog);
  }


  public void batchInsert(List<PayTransferLog> list) throws ServiceException {
    payTransferLogDao.batchInsert(list);
  }


  public int queryNumByStatusAndBatchNo(int status, String batchNo) throws ServiceException {
    return payTransferLogDao.queryNumByStatusAndBatchNo(status, batchNo);
  }
}
