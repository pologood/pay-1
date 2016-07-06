package com.sogou.pay.service.service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayTransferDao;
import com.sogou.pay.service.entity.PayTransfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayTransferService {

  @Autowired
  private PayTransferDao payTransferDao;


  public void insert(PayTransfer PayTransfer) throws ServiceException {
    payTransferDao.insert(PayTransfer);
  }


  public void batchInsert(List<PayTransfer> list) throws ServiceException {
    payTransferDao.batchInsert(list);
  }


  public List<PayTransfer> queryByBatchNo(String appId, String batchNo) {
    return payTransferDao.queryByBatchNo(appId, batchNo);
  }


  public List<PayTransfer> queryRefund(String startTime, String endTime, String recBankacc, String recName) {
    return payTransferDao.queryRefund(startTime, endTime, recBankacc, recName);
  }


  public List<PayTransfer> queryByBatchNoAndStatus(String batchNo, int payStatus) {
    return null;
  }


  public void updateStatusById(String id, int status, String remark) throws ServiceException {
    payTransferDao.updateStatusById(id, status, remark);
  }


  public void updateStatusBySerialNo(String serialNo, int status, String resultDesc) {
    payTransferDao.updateStatusBySerialNo(serialNo, status, resultDesc);
  }


  public PayTransfer queryBySerialNo(String serialNo) {
    return payTransferDao.queryBySerialNo(serialNo);
  }


  public void updateStatusByBatchNo(String appId, String batchNo, int status) {
    payTransferDao.updateStatusByBatchNo(appId, batchNo, status);
  }


  public List<PayTransfer> queryByOutRefAndAppId(List<String> orderIdList, int appId)
          throws ServiceException {
    return payTransferDao.queryByOutRefAndAppId(orderIdList, appId);
  }

}
