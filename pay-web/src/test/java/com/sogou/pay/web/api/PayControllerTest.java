package com.sogou.pay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.PayController;
import com.sogou.pay.web.form.PayParams;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Author huangguoqing
 * @Date 2015/3/5 15:55
 * @Description:
 */
public class PayControllerTest extends BaseTest {
    @Autowired
    PayController controller;
    
    @Test
    public void testPayWeb() {
        //String url = "/pay/doPay";
        String url = "/gw/pay/web";
        PayParams params = new PayParams();
        params.setVersion("v1.0");
        params.setOrderId("ORDERID1000000");
        params.setOrderAmount("50.02");
        params.setOrderTime("20150305112122");
        params.setProductName("测试商品");
        params.setProductNum("2");
        params.setProductDesc("测试商品描述");
        params.setBankId("ALIPAY");
        params.setAppId("1999");
        params.setAccessPlatform("1");
        params.setSignType("0");
        params.setPageUrl("http://127.0.0.1:8080/notify/ali/pay/testBgUrl");
        params.setBgUrl("http://127.0.0.1:8080/notify/ali/pay/testBgUrl");
        params.setBankCardType("");
        Map map = BeanUtil.Bean2Map(params);

        map.put("sign", JSONObject.parse(controller.signData(map, null)));
        testGet(url, map);
    }

    
    @Test
    public void testDoPayForWechat() {
        //String url = "/pay/doPayForWechat.j";
        String url = "/api/pay/qrcode";
        PayParams params = new PayParams();
        params.setVersion("v1.0");
        params.setOrderId("ORDERID1000000");
        params.setOrderAmount("50.02");
        params.setOrderTime("20150305112122");
        params.setProductName("测试商品");
        params.setProductNum("2");
        params.setProductDesc("测试商品描述");
        params.setBankId("WECHAT");
        params.setAppId("1999");
        params.setAccessPlatform("1");
        params.setSignType("0");
        params.setPageUrl("http://127.0.0.1:8080/notify/ali/pay/testBgUrl");
        params.setBgUrl("http://127.0.0.1:8080/notify/ali/pay/testBgUrl");
        Map map = BeanUtil.Bean2Map(params);
        map.put("sign", JSONObject.parse(controller.signData(map, null)));
        testGet(url, map);
    }
}
