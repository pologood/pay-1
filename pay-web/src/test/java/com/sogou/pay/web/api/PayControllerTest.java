package com.sogou.pay.web.api;

import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.APIController;
import com.sogou.pay.web.controller.api.GWController;
import com.sogou.pay.web.form.PayForm;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;


public class PayControllerTest extends BaseTest {

    @Autowired
    APIController apiController;

    @Autowired
    GWController gwController;

    @Autowired
    SequenceFactory sequenceFactory;
    
    @Test
    public void testPayWeb() {
        String url = "/gw/pay/web";
        PayForm params = new PayForm();
        params.setVersion("v1.0");
        params.setOrderId(sequenceFactory.getOrderNo());
        params.setOrderAmount("0.02");
        params.setOrderTime(DateUtil.format(new Date(), DateUtil.DATE_FORMAT_SECOND_SHORT));
        params.setProductName("测试商品");
        params.setProductNum("1");
        params.setProductDesc("测试商品描述");
        params.setBankId("TEST_TENPAY");
        params.setAppId("1000");
        params.setAccessPlatform("1");
        params.setSignType("0");
        params.setPageUrl("http://center.pay.sogou.com/notify/testBgUrl");
        params.setBgUrl("http://center.pay.sogou.com/notify/testBgUrl");
        params.setBankCardType("");
        Map map = BeanUtil.Bean2Map(params);

        map.put("sign", apiController.signData(map));
        testGet(url, map);
    }

    @Test
    public void testPayWap() {
        String url = "/gw/pay/wap";
        PayForm params = new PayForm();
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

        map.put("sign", apiController.signData(map));
        testGet(url, map);
    }


    @Test
    public void testPayQRCode() {
        String url = "/api/pay/qrcode";
        PayForm params = new PayForm();
        params.setVersion("v1.0");
        params.setOrderId(sequenceFactory.getOrderNo());
        params.setOrderAmount("0.02");
        params.setOrderTime(DateUtil.format(new Date(), DateUtil.DATE_FORMAT_SECOND_SHORT));
        params.setProductName("测试商品");
        params.setProductNum("2");
        params.setProductDesc("测试商品描述");
        params.setBankId("WECHAT");
        params.setAppId("1999");
        params.setAccessPlatform("1");
        params.setSignType("0");
        params.setPageUrl("http://center.pay.sogou.com/notify/testBgUrl");
        params.setBgUrl("http://center.pay.sogou.com/notify/testBgUrl");
        Map map = BeanUtil.Bean2Map(params);
        map.put("sign", apiController.signData(map));
        testGet(url, map);
    }

    @Test
    public void testPaySDK() {
        String url = "/api/pay/sdk";
        PayForm params = new PayForm();
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

        map.put("sign", apiController.signData(map));
        testGet(url, map);
    }


}
