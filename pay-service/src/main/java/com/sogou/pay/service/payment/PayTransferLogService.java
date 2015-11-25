package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayTransferLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by qibaichao on 2015/6/2.
 * 代付日志
 */
public interface PayTransferLogService {

    public void insert(PayTransferLog payTransferLog) throws ServiceException;

    public void batchInsert(List<PayTransferLog> list)throws ServiceException;

    public int queryNumByStatusAndBatchNo(int status, String batchNo) throws ServiceException;
}
