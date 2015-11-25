package com.sogou.pay.web.api;

import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.MD5Util;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.PayController;
import com.sogou.pay.web.form.PayParams;
import com.sogou.pay.web.form.RefundParams;
import com.sogou.pay.web.form.notify.AliRefundNotifyParams;
import com.sogou.pay.web.form.notify.TenRefundNotifyParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/16 11:11
 */
public class RefundControllerTest extends BaseTest {
    @Autowired
    PayController controller;


    @Test
    public void testTenRefund() {
        String url = "/refund";
        RefundParams params = new RefundParams();
        params.setAppId("1999");
        params.setBgUrl("http://sg.pay.sogou.com/pay-web/notify/ali/pay/webAsync");
        params.setSignType("MD5");
        params.setSign("8b48f9376069eb1b654d7bed8e025b9f");
        params.setOrderId("TESTORDER20150428163308288001");
        params.setRefundAmount("0.01");
        Map map = BeanUtil.beanToMap(params);
        testGet(url, map);
    }

    @Test
    public void testAliRefund() {
        String url = "/refund";
        RefundParams params = new RefundParams();
        params.setAppId("1999");
        params.setBgUrl("http://sg.pay.sogou.com/pay-web/notify/ali/pay/webAsync");
        params.setSignType("MD5");
        params.setSign("8b48f9376069eb1b654d7bed8e025b9f");
        params.setOrderId("OD20150430165852822");
        params.setRefundAmount("0.01");
        Map map = BeanUtil.beanToMap(params);
        testGet(url, map);
    }
}
