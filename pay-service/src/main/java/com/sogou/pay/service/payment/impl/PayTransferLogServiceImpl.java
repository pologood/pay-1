package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayTransferLogDao;
import com.sogou.pay.service.entity.PayTransferLog;
import com.sogou.pay.service.payment.PayTransferLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qibaichao on 2015/6/2.
 * 代付日志
 */
@Service
public class PayTransferLogServiceImpl implements PayTransferLogService {

    @Autowired
    private PayTransferLogDao payTransferLogDao;

    @Override
    public void insert(PayTransferLog payTransferLog) throws ServiceException {
        payTransferLogDao.insert(payTransferLog);
    }

    @Override
    public void batchInsert(List<PayTransferLog> list) throws ServiceException {
        payTransferLogDao.batchInsert(list);
    }

    @Override
    public int queryNumByStatusAndBatchNo(int status, String batchNo) throws ServiceException {
        return payTransferLogDao.queryNumByStatusAndBatchNo(status, batchNo);
    }
}
