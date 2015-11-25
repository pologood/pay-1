package com.sogou.pay.service.payment;

import java.util.List;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayTransfer;
import com.sogou.pay.service.utils.AppXmlPacket;

/**
 * Created by qibaichao on 2015/6/2.
 * 代付单
 */
public interface PayTransferService {

    public void insert(PayTransfer PayTransfer) throws ServiceException;

    public void batchInsert(List<PayTransfer> list)throws ServiceException;

    public void updateStatusByBatchNo(String appId,String batchNo, int status);

    public void updateStatusById(String id, int status, String remark) throws ServiceException;

    public void updateStatusBySerialNo(String tranferNo, int status, String resultDesc) ;

    public PayTransfer queryBySerialNo(String serialNo);

    public List<PayTransfer> queryByBatchNo(String appId,String batchNo);

    public List<PayTransfer> queryRefund(String startTime, String endTime, String recBankacc, String recName);

    public List<PayTransfer> queryByBatchNoAndStatus(String batchNo,int payStatus);
    
    public List<PayTransfer> queryByOutRefAndAppId (List<String> recordList,int appId) throws ServiceException;
}
