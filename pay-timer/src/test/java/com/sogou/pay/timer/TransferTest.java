package com.sogou.pay.timer;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.timer.transfer.TransferManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 代付查询任务
 */
public class TransferTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(TransferTest.class);

    @Autowired
    private TransferManager transferManager;

    @Test
    public void testTransfer() {
        String appId="1999";
        String batchNo = "20151012172007358004";
        Result result = transferManager.transfer(appId,batchNo);
        System.out.println(JSONUtil.Bean2JSON(result));
    }

    @Test
    public void testQuery() {
        String appId="1999";
        String batchNo = "20160126161500157001";
        transferManager.queryTransfer(appId,batchNo);
    }

    @Test
    public void testQueryRefund() {
        String beginDate = "20141221";
        String endDate = "20141221";
        transferManager.queryTransferRefund(beginDate, endDate);
    }

//    @Test
//    public void doNotify() {
//        String notifyDate = "20150617";
//        payTranferNotifyJob.doProcess(notifyDate);
//    }

}
