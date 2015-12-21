package com.sogou.pay.service.dao;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.service.BaseTest;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.enums.PayTransferBatchStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * Created by qibaichao on 2015/6/1.
 */
public class PayTransferBatchDaoTest extends BaseTest {


    @Autowired
    private PayTransferBatchDao payTransferBatchDao;

    @Test
    public void insert() {
        PayTransferBatch payTransferBatch = new PayTransferBatch();
        payTransferBatch.setAppId(1999);
        payTransferBatch.setBatchNo("2015061601ddd");
        payTransferBatch.setCompanyName("搜狗网络");
        //转出帐号
        payTransferBatch.setDbtAcc("591902896010504");
        //分行代码 10北京
        payTransferBatch.setBbkNbr("59");
        //业务类别
        payTransferBatch.setBusCod("N03020");
        //业务模式编号
        payTransferBatch.setBusMod("00001");
        //代发其他
        payTransferBatch.setTrsTyp("BYBK");
        payTransferBatch.setAuditDesc("审批通过");
        payTransferBatch.setPlanTotal(1);
        payTransferBatch.setPlanAmt(BigDecimal.valueOf(10.01));
        payTransferBatch.setMemo("代发");
        payTransferBatchDao.insert(payTransferBatch);
    }

    @Test
    public void updateById() {
        PayTransferBatch payTransferBatch = new PayTransferBatch();
        payTransferBatch.setBatchNo("2015061601");
        payTransferBatch.setTradeState(PayTransferBatchStatus.AUDIT_PASS.getValue());
        int num = payTransferBatchDao.updateTransferBatch(payTransferBatch);
        System.out.println(num);
    }

    @Test
    public void queryByBatchNo() {
        String batchNo = "1";
        PayTransferBatch payTransferBatch = payTransferBatchDao.queryByBatchNo(null,batchNo);
        System.out.println(JSON.toJSON(payTransferBatch));
    }

    @Test
    public void updateTradeStatusByBatchNo() {
        String appId = "1999";
        String batchNo = "1231737712";
        int status = 6;
        int num =  payTransferBatchDao.updateTradeStatusByBatchNo(appId,batchNo,status,null);
        System.out.println(num);
    }

}
