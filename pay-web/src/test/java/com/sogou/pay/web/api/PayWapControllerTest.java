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
public class PayWapControllerTest extends BaseTest {
    @Autowired
    PayController controller;
    
    @Test
    public void testPayWap() {
        //String url = "/paywap/doPay";
        String url = "/gw/pay/wap";
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
        params.setAccessPlatform("2");
        params.setSignType("0");
        params.setPageUrl("http://center.pay.sogou.com/notify/testBgUrl");
        params.setBgUrl("http://center.pay.sogou.com/notify/testBgUrl");
        params.setBankCardType("");
        Map map = BeanUtil.Bean2Map(params);

        map.put("sign", JSONObject.parse(controller.signData(map)));
        testGet(url, map);
    }

}
