package com.sogou.pay.service.payment.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayTransferBatchDao;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.payment.PayTransferBatchService;

/**
 * Created by qibaichao on 2015/6/2.
 * 代付任务
 */
@Service
public class PayTransferBatchServiceImpl implements PayTransferBatchService {

    @Autowired
    private PayTransferBatchDao payTransferBatchDao;

    @Override
    public void insert(PayTransferBatch payTransferBatch) throws ServiceException {
        payTransferBatchDao.insert(payTransferBatch);
    }

    @Override
    public PayTransferBatch queryByAppIdAndBatchNo(String appId,String batchNo) {
        return payTransferBatchDao.queryByAppIdAndBatchNo(appId,batchNo);
    }

    @Override
    public List<PayTransferBatch> queryByTradeStatus(int tradeStatus) {
        return payTransferBatchDao.queryByTradeStatus(tradeStatus);
    }

    @Override
    public List<PayTransferBatch> queryByNotifyFlag(int nofityFlag,String notifyDate) {
        return payTransferBatchDao.queryByNotifyFlag(nofityFlag,notifyDate);
    }

    @Override
    public void updateTradeStatusByBatchNo(String batchNo, int tradeStatus, String resultDesc) {
        payTransferBatchDao.updateTradeStatusByBatchNo(batchNo, tradeStatus, resultDesc);
    }

    @Override
    public void updateByBatchNo(PayTransferBatch payTransferBatch) {
        payTransferBatchDao.updateByBatchNo(payTransferBatch);
    }

    @Override
    public void updateNotifyFlagByBatchNo(String batchNo, int nofityFlag) {
        payTransferBatchDao.updateNotifyFlagByBatchNo(batchNo, nofityFlag);
    }

}
