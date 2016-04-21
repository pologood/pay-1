package com.sogou.pay.service;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.BaseTest;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.manager.job.PayTranferNotifyJob;
import com.sogou.pay.manager.job.PayTranferTicketRefundQueryJob;
import com.sogou.pay.manager.model.Record;
import com.sogou.pay.manager.payment.PayTranferRequestManager;
import com.sogou.pay.manager.payment.PayTransferQueryManager;
import com.sogou.pay.service.payment.PayTransferBatchService;
import com.sogou.pay.service.utils.orderNoGenerator.PayTransferBatchNo;
import com.sogou.pay.common.utils.SequenceGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by qibaichao on 2015/6/16.
 * 代付查询任务
 */
public class PayTransferTest extends BaseTest {

    private static final Logger httpClientTimingLogger = LoggerFactory.getLogger("httpClientTimingLogger");


    @Autowired
    private PayTransferBatchService payTransferBatchService;

    @Autowired
    private PayTransferQueryManager payTransferQueryManager;

    @Autowired
    private PayTranferRequestManager payTranferRequestManager;

    @Autowired
    private PayTranferTicketRefundQueryJob payTranferTicketRefundQueryJob;

    @Autowired
    private PayTranferNotifyJob payTranferNotifyJob;

    @Autowired
    private PayTransferBatchNo payTransferBatchNo;

    @Autowired
    private SequenceGenerator payTransferNo;

    public static void main(String args[]) {
//        System.out.println("20150624115414935001".length());
//        StringBuffer sb = new StringBuffer();
//        sb.append("version=V1.0&appId=1999&batchNo=201506171055014531&companyName=搜狗科技&bbkNbr=10&memo=代付款&signType=0&sign=test");
//        Record record = null;
//        List<Record> list = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            record = new Record();
//            record.setPayId(i + "");
//            record.setRecBankacc("6225880127530773");
//            record.setRecName("黄国庆");
//            record.setPayAmt("10");
//            list.add(record);
//        }
//        sb.append("&recordList=" + JSON.toJSON(list));
//        String str = HttpUtil.sendPost("http://10.129.41.33:8020/pay-web/payTrans/doPay.j", sb.toString());
//        System.out.println(str);

        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("a");
        list.add("c");
        list.add("a");
        list.add("a");
        list.add("c");
        list.add("a");
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Integer num = map.get(list.get(i));
            if (num == null) {
                map.put(list.get(i), 1);
            } else {
                map.put(list.get(i), num + 1);
            }
        }
        System.out.println(map.toString());
        System.out.println((int)7/2);
    }

    @Test
    public void request() {
        String appId="1999";
        String batchNo = "20151012172007358004";
        Result result = payTranferRequestManager.doProcess(appId,batchNo);
        System.out.println(JSON.toJSON(result));
    }

    @Test
    public void doQuery() {
        String appId="1999";
        String batchNo = "20160126161500157001";
        payTransferQueryManager.doProcess(appId,batchNo);
    }

    @Test
    public void doRefundQuery() {

        String beginDate = "20141221";
        String endDate = "20141221";
        payTranferTicketRefundQueryJob.doQuery(beginDate, endDate);
    }

    @Test
    public void doNotify() {
        String notifyDate = "20150617";
        payTranferNotifyJob.doProcess(notifyDate);
    }

    @Test
    public void testUrl() {

        try {
            //http://localhost:8080/payTrans/doPay?version=1.0&batchNo=1234&appId=1999&companyName=%E6%90%9C%E7%8B%97%E7%A7%91%E6%8A%80&dbtAcc=591902896010504&bbkNbr=59&memo=%E4%BB%A3%E5%8F%91&recordList=
            StringBuffer sb = new StringBuffer();
            String batchNo = payTransferBatchNo.getNo();
            //sb.append(" http://localhost:8080/payTrans/doPay?");
            //sb.append("&");
            sb.append("version=1.0&batchNo="+batchNo+"&appId=1999&companyName=搜狗科技&dbtAcc=591902896010504&bbkNbr=59&memo=代发&&signType=0&sign=50D29CF5740C27E9419CB9BDA426B3A6");
            String recordStr = "";
            Record record = null;
            List<Record> list = new ArrayList<>();
            for (int i = 0; i < 1; i++) {
                record = new Record();
                record.setPayId(payTransferNo.getNo());
                record.setRecBankacc("6225885910000108");
                record.setRecName("Judy Zeng");
//                record.setBankFlg("");
//                record.setDesc("");
//                record.setEacCity("");
                record.setPayAmt("1.00");
                list.add(record);

            }
            sb.append("&recordList=" + JSON.toJSON(list));
            HttpUtil.sendPost("http://test.web.pay.sogou/payTrans/doPay", sb.toString());
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
