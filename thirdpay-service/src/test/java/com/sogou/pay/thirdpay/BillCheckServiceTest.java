package com.sogou.pay.thirdpay;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.thirdpay.biz.BillCheckService;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.modle.OutCheckRecord;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by qibaichao on 2015/6/29.
 */
public class BillCheckServiceTest  extends BaseTest {

    @Autowired
    private BillCheckService billCheckService;

    @Test
    public void doPayQuery() {

        try {
            String startTime = "20150626235959";
            String endTime = "20150628235959";
            String pageNo = "1";
            String merchantNo = "10012138842";
            String key = "5UHQX2G65W4ECF5G";
            ResultMap resultMap = billCheckService.doPayQuery(merchantNo, startTime, endTime, pageNo, key);
            List<OutCheckRecord> records = (List<OutCheckRecord>) resultMap.getData().get("records");
            System.out.println("records:"+ JSON.toJSON(records));
//            System.out.println("records size:"+ records.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void doRefundQuery() {

        try {
            String startTime = "20130424";
            String endTime = "20130426";
            String pageNo = "1";
            String merchantNo = "10012143357";
            String key = "4IQLTUNT365Z6N4K";

            ResultMap resultMap = billCheckService.doRefundQuery(merchantNo, startTime, endTime, pageNo, key);
            List<OutCheckRecord> records = (List<OutCheckRecord>) resultMap.getData().get("records");
            System.out.println("records:"+ JSON.toJSON(records));
//            System.out.println("records size:"+ records.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
