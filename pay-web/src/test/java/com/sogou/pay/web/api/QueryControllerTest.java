package com.sogou.pay.web.api;

import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.APIController;
import com.sogou.pay.web.form.RefundQueryForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class QueryControllerTest extends BaseTest {
    @Autowired
    APIController controller;

    @Test
    public void testOrderQueryPay() {
        String url = "/api/pay/query";
        RefundQueryForm params = new RefundQueryForm();
        params.setOrderId("OD20160622195216784");
        params.setSignType("0");
        params.setAppId("1999");
        Map map = BeanUtil.Bean2Map(params);
        map.put("sign", controller.signData(map).getItem("sign"));
        testGet(url, map);
    }

    @Test
    public void testOrderQueryRefund() {
        String url = "/api/refund/query";
        RefundQueryForm params = new RefundQueryForm();
        params.setOrderId("OD20160622195216784");
        params.setSignType("0");
        params.setAppId("1999");
        Map map = BeanUtil.Bean2Map(params);
        map.put("sign", controller.signData(map).getItem("sign"));
        testGet(url, map);
    }

}
