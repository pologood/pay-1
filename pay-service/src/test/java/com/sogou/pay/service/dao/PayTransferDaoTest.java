package com.sogou.pay.service.dao;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.PayTransfer;
import com.sogou.pay.service.enums.PayTransferStatus;
import com.sogou.pay.common.utils.SequenceGenerator;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qibaichao on 2015/6/1.
 */
public class PayTransferDaoTest extends BaseTest {

    @Autowired
    private PayTransferDao payTransferDao;

    @Autowired
    private SequenceGenerator payTransferNo;

    @Test
    public void insert() {

        try {
            PayTransfer payTransfer = new PayTransfer();
            payTransfer.setAppId(1999);
            payTransfer.setBatchNo("20150616");
            payTransfer.setOutRef("123");
            payTransfer.setRecBankAcc("6225885910000108");
            payTransfer.setRecName("Judy Zeng");
            payTransfer.setPayAmt(BigDecimal.valueOf(10.01));
            payTransfer.setFee(BigDecimal.valueOf(1));
            payTransfer.setPayDesc(payTransferNo.getNo());
            payTransfer.setSerialNo("sss");
            payTransferDao.insert(payTransfer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void batchInsert() {
        try {
            List<PayTransfer> list = new ArrayList<>();
            PayTransfer payTransfer = null;
            for (int i = 0; i < 100; i++) {
                payTransfer = new PayTransfer();
                payTransfer.setAppId(1999);
                payTransfer.setBatchNo("1");
                payTransfer.setOutRef(i + "");
                payTransfer.setRecBankAcc("收款方银行帐号");
                payTransfer.setRecName("收款方真实姓名");
                payTransfer.setPayAmt(BigDecimal.ONE);
                payTransfer.setPayDesc("营销代付");
                payTransferDao.insert(payTransfer);
                list.add(payTransfer);
            }
            payTransferDao.batchInsert(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queryByBatchNo() {
        String appId="1999";
        String batchNo = "1";
        List<PayTransfer> list = payTransferDao.queryByBatchNo(appId,batchNo);
        System.out.println(JSON.toJSON(list));

    }

    @Test
    public void updateStatusById() {
        String id = "1";
        int status = 1;
        String remark = "sssss";
        int num = payTransferDao.updateStatusById(id, status, remark);
    }

    @Test
    public void updateStatusByBatchNo() {
        String appId="1999";
        String batchNo = "1";
        int num = payTransferDao.updateStatusByBatchNo(appId,batchNo, PayTransferStatus.INIT.getValue());
        System.out.println(num);
    }

    @Test
    public void queryByOutRefAndAppId() {
        List<String> list = new ArrayList<String>();
        list.add("123");
        list.add("1");
        List<PayTransfer> list2 = payTransferDao.queryByOutRefAndAppId(list, 1999);
        System.out.println(list2.size());
    }

    @Test
    public void queryRefund() {
        String startTime = "2015-06-16";
        String endTime = "2015-06-16";
        String recBankacc = "6225880127530774";
        String recName = "张三";
        List<PayTransfer> list = payTransferDao.queryRefund(startTime,endTime,recBankacc,recName);
        System.out.println(list.size());
    }

}
