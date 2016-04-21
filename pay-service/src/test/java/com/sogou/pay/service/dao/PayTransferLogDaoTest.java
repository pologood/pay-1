package com.sogou.pay.service.dao;

import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.PayTransferLog;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qibaichao on 2015/6/1.
 */
public class PayTransferLogDaoTest extends BaseTest {

    @Autowired
    private PayTransferLogDao payTransferLogDao;

    @Test
    public void insert(){
        PayTransferLog payTransferLog = new PayTransferLog();
        payTransferLog.setBatchNo("batchNo");
        payTransferLog.setRemark("success");
        payTransferLog.setSerial("123");
        payTransferLog.setStatus(1);
        payTransferLogDao.insert(payTransferLog);
    }
    @Test
    public void batchInsert(){
        List<PayTransferLog> list = new ArrayList<PayTransferLog>();
        PayTransferLog payTransferLog = new PayTransferLog();
        payTransferLog.setBatchNo("batchNo");
        payTransferLog.setRemark("success");
        payTransferLog.setSerial("123");
        payTransferLog.setStatus(1);
        list.add(payTransferLog);
         payTransferLog = new PayTransferLog();
        payTransferLog.setBatchNo("batchNo");
        payTransferLog.setRemark("success");
        payTransferLog.setSerial("123");
        payTransferLog.setStatus(1);
        list.add(payTransferLog);
        payTransferLogDao.batchInsert(list);
    }


    @Test
    public void queryNumByStatusAndBatchNo(){
        int status =1;
        String batchNo="batchNo";
        int num =payTransferLogDao.queryNumByStatusAndBatchNo(status,batchNo);
        System.out.println(num);
    }
}
