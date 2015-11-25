package com.sogou.pay.service.payment.manager;

import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.manager.model.notify.PayNotifyModel;
import com.sogou.pay.manager.notify.PayNotifyManager;
import com.sogou.pay.service.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by qibaichao on 2015/4/7.
 */
public class PayNotifyManagerTest extends BaseTest {

    @Autowired
    private PayNotifyManager payNotifyManager;

    @Test
    public void  doProcess(){

        PayNotifyModel payNotifyModel = new PayNotifyModel();
        payNotifyModel.setPayDetailId("ZF20150407173332162001");
        payNotifyModel.setAgencyOrderId("1234274801201504070632958812");
        payNotifyModel.setAgencyPayTime(new Date());
        payNotifyModel.setTrueMoney(BigDecimal.valueOf(0.01));

        payNotifyManager.doProcess(payNotifyModel);
    }
}
