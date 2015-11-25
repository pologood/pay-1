package com.sogou.pay.service.payment;

import java.util.List;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayTransferBatch;

/**
 * Created by qibaichao on 2015/6/2.
 * 代付任务
 */
public interface PayTransferBatchService {

    public void insert(PayTransferBatch payTransferBatch) throws ServiceException;

    public PayTransferBatch queryByAppIdAndBatchNo(String appId,String batchNo);

    public List<PayTransferBatch> queryByTradeStatus(int tradeStatus);

    public List<PayTransferBatch> queryByNotifyFlag(int nofityFlag, String norifyDate);

    public void updateTradeStatusByBatchNo(String batchNo, int tradeStatus, String resultDesc);

    public void updateByBatchNo(PayTransferBatch payTransferBatch);

    public void updateNotifyFlagByBatchNo(String batchNo, int nofityFlag);

}
