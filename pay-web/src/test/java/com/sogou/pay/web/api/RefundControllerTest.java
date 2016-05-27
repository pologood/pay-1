package com.sogou.pay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.PayController;
import com.sogou.pay.web.form.RefundParams;
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
    public void testRefund() {
        //String url = "/refund";
        String url = "/api/refund";
        RefundParams params = new RefundParams();
        params.setAppId("1000");
        params.setBgUrl("http://center.pay.sogou.com/notify/testBgUrl");
        params.setSignType("0");
        params.setOrderId("OD20160526172234360");
        //params.setRefundAmount("");
        Map map = BeanUtil.Bean2Map(params);
        map.put("sign", JSONObject.parse(controller.signData(map)));
        testGet(url, map);
    }
}
