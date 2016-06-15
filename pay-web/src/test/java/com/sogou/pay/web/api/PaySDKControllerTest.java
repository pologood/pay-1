package com.sogou.pay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.APIController;
import com.sogou.pay.web.form.PayParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Author huangguoqing
 * @Date 2015/3/5 15:55
 * @Description:
 */
public class PaySDKControllerTest extends BaseTest {
    @Autowired
    APIController controller;
    
    @Test
    public void testPaySDK() {
        //String url = "/paysdk/doPay";
        String url = "/api/pay/sdk";
        PayParams params = new PayParams();
        params.setVersion("v1.0");
        params.setOrderId("ORDERID1000000");
        params.setOrderAmount("50.02");
        params.setOrderTime("20150305112122");
        params.setProductName("测试商品");
        params.setProductNum("2");
        params.setProductDesc("测试商品描述");
        params.setBankId("WECHAT");
        params.setAppId("5000");
        params.setAccessPlatform("3");
        params.setSignType("0");
        params.setPageUrl("http://center.pay.sogou.com/notify/testBgUrl");
        params.setBgUrl("http://center.pay.sogou.com/notify/testBgUrl");
        params.setBankCardType("");
        Map map = BeanUtil.Bean2Map(params);

        map.put("sign", JSONObject.parse(controller.signData(map)));
        testGet(url, map);
    }
}
