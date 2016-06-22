package com.sogou.pay.web.api;

import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.APIController;
import com.sogou.pay.web.form.RefundForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;


public class RefundControllerTest extends BaseTest {
    @Autowired
    APIController controller;

    @Test
    public void testRefund() {
        String url = "/api/refund";
        RefundForm params = new RefundForm();
        params.setAppId("1000");
        params.setBgUrl("http://center.pay.sogou.com/notify/testBgUrl");
        params.setSignType("0");
        params.setOrderId("OD20160601180233150");
        Map map = BeanUtil.Bean2Map(params);
        map.put("sign", controller.signData(map).getItem("sign"));
        testGet(url, map);
    }
}
