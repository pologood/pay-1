package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.manager.model.Record;
import com.sogou.pay.service.dao.PayTransferDao;
import com.sogou.pay.service.entity.PayTransfer;
import com.sogou.pay.service.payment.PayTransferService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by qibaichao on 2015/6/2.
 */
@Service
public class PayTransferServiceImpl implements PayTransferService {

    @Autowired
    private PayTransferDao payTransferDao;

    @Override
    public void insert(PayTransfer PayTransfer) throws ServiceException {
        payTransferDao.insert(PayTransfer);
    }

    @Override
    public void batchInsert(List<PayTransfer> list) throws ServiceException {
        payTransferDao.batchInsert(list);
    }

    @Override
    public List<PayTransfer> queryByBatchNo(String appId,String batchNo) {
        return payTransferDao.queryByBatchNo(appId,batchNo);
    }

    @Override
    public List<PayTransfer> queryRefund(String startTime, String endTime, String recBankacc, String recName) {
        return payTransferDao.queryRefund(startTime, endTime, recBankacc, recName);
    }

    @Override
    public List<PayTransfer> queryByBatchNoAndStatus(String batchNo, int payStatus) {
        return null;
    }


    @Override
    public void updateStatusById(String id, int status, String remark) throws ServiceException {
        payTransferDao.updateStatusById(id, status, remark);
    }

    @Override
    public void updateStatusBySerialNo(String serialNo, int status, String resultDesc) {
        payTransferDao.updateStatusBySerialNo(serialNo, status, resultDesc);
    }

    @Override
    public PayTransfer queryBySerialNo(String serialNo) {
        return payTransferDao.queryBySerialNo(serialNo);
    }

    @Override
    public void updateStatusByBatchNo(String appId,String batchNo, int status) {
        payTransferDao.updateStatusByBatchNo(appId,batchNo, status);
    }

    @Override
    public List<PayTransfer> queryByOutRefAndAppId(List<String> orderIdList, int appId)
            throws ServiceException {
        return payTransferDao.queryByOutRefAndAppId(orderIdList, appId);
    }

}
