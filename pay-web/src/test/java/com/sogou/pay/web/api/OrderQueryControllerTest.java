package com.sogou.pay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.PayController;
import com.sogou.pay.web.form.QueryRefundParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/16 11:11
 */
public class OrderQueryControllerTest extends BaseTest {
    @Autowired
    PayController controller;

    @Test
    public void testOrderQueryPay() {
        //String url = "/orderQuery/pay";
        String url = "/api/pay/query";
        QueryRefundParams params = new QueryRefundParams();
        params.setOrderId("OD20160526172749123");
        params.setSignType("0");
        params.setAppId("1000");
        Map map = BeanUtil.Bean2Map(params);
        map.put("sign", JSONObject.parse(controller.signData(map)));
        testGet(url, map);
    }

    @Test
    public void testOrderQueryRefund() {
        //String url = "/orderQuery/refund";
        String url = "/api/refund/query";
        QueryRefundParams params = new QueryRefundParams();
        params.setOrderId("OD20160530111427742");
        params.setSignType("0");
        params.setAppId("1000");
        Map map = BeanUtil.Bean2Map(params);
        map.put("sign", JSONObject.parse(controller.signData(map)));
        testGet(url, map);
    }

}
