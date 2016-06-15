package com.sogou.pay.service.payment;

import com.sogou.pay.common.Model.AppRefundNotifyModel;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.BaseTest;
import com.sogou.pay.service.connect.QueueNotifyProducer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * Created by qibaichao on 2015/4/9.
 */
public class QueuePayNotifyProducerTest extends BaseTest {


    @Autowired
    private QueueNotifyProducer queueNotifyProducer;


    @Test
    public void sendPayMessage() {

        PMap map = new PMap();
        map.put("isSuccess", "T");
        map.put("appId", 1999);
        map.put("signType", "0");
        map.put("orderId", "123");
        map.put("payId", "123");
        map.put("orderMoney", "10");
        map.put("tradeStatus", "TRADE_FINISHED");
        map.put("paySuccessTime", "1234");
        map.put("appBgUrl", "url");
        queueNotifyProducer.sendPayMessage(map);
    }

    @Test
    public void sendRefundMessage() {

    }

    @Test
    public void test() {
        String totalFee = "0.02";
        BigDecimal true_money = new BigDecimal(totalFee).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UP);
        BigDecimal outMoney = BigDecimal.valueOf(0.01);
        if (true_money.compareTo(outMoney) != 0) { //BigDecimal不能用equals

            System.out.println("eee");
        }

    }

}
