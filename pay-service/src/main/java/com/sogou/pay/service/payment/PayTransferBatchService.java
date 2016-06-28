package com.sogou.pay.service.payment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayTransferBatchDao;
import com.sogou.pay.service.entity.PayTransferBatch;


@Service
public class PayTransferBatchService {

  @Autowired
  private PayTransferBatchDao payTransferBatchDao;


  public void insert(PayTransferBatch payTransferBatch) throws ServiceException {
    payTransferBatchDao.insert(payTransferBatch);
  }


  public PayTransferBatch queryByBatchNo(String appId, String batchNo) {
    return payTransferBatchDao.queryByBatchNo(appId, batchNo);
  }


  public List<PayTransferBatch> queryByTradeStatus(int tradeStatus) {
    return payTransferBatchDao.queryByTradeStatus(tradeStatus);
  }


  public List<PayTransferBatch> queryByNotifyFlag(int nofityFlag, String notifyDate) {
    return payTransferBatchDao.queryByNotifyFlag(nofityFlag, notifyDate);
  }


  public void updateTradeStatusByBatchNo(String appId, String batchNo, int tradeStatus, String resultDesc) {
    payTransferBatchDao.updateTradeStatusByBatchNo(appId, batchNo, tradeStatus, resultDesc);
  }


  public void updateTransferBatch(PayTransferBatch payTransferBatch) {
    payTransferBatchDao.updateTransferBatch(payTransferBatch);
  }


  public void updateNotifyFlagByBatchNo(String appId, String batchNo, int nofityFlag) {
    payTransferBatchDao.updateNotifyFlagByBatchNo(appId, batchNo, nofityFlag);
  }


  public PayTransferBatch queryByYurref(String Yurref) {
    return payTransferBatchDao.queryByYurref(Yurref);
  }
}
