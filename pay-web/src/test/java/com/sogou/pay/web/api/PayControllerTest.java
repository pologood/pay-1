package com.sogou.pay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.MD5Util;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.PayController;
import com.sogou.pay.web.form.PayParams;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.ResourceBundle;

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
        String url = "/pay/doPay";
        PayParams params = new PayParams();
        params.setVersion("V1.0");
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
        params.setPageUrl("http://127.0.0.1:8080/pageUrl.html");
        params.setBgUrl("http://127.0.0.1:8080/bgUrl.html");
        params.setBankCardType("");
        Map map = BeanUtil.beanToMap(params);
        
        map.put("sign", "b1af584504b8e845ebe40b8e0e733729");
        testGet(url, map);
    }
    
    @Test
    public void testPayWebVersionNULL() {
        String url = "/pay/doPay";
        PayParams params = new PayParams();
        params.setVersion(null);
        params.setOrderId("ORDERID1000000");
        params.setOrderAmount("50.02");
        params.setOrderTime("20150305112122");
        params.setProductName("测试商品");
        params.setProductNum("2");
        params.setProductDesc("测试商品描述");
        params.setBankId("TENPAY");
        params.setAppId("1999");
        params.setAccessPlatform("1");
        params.setSignType("0");
        params.setPageUrl("http://127.0.0.1:8080/pageUrl.html");
        params.setBgUrl("http://127.0.0.1:8080/bgUrl.html");
        params.setBankCardType("");
        Map map = BeanUtil.beanToMap(params);
        map.put("sign", MD5Util.MD5Encode(params.getVersion()+"&"+params.getOrderId()+"&"+params.getOrderAmount()
                +"&"+params.getOrderTime()+"&"+params.getAppId()+"&"+params.getSignType()+"&"+params.getAccessPlatform()
                +"862653da5865293b1ec8cc","utf-8"));
        testGet(url, map);
    }
    
    @Test
    public void testDoPayForWechat() {
        String url = "/pay/doPayForWechat.j";
        PayParams params = new PayParams();
        params.setVersion(null);
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
        params.setPageUrl("http://center.pay.sogou.com/pay-web/notify/ali/pay/testBgUrl");
        params.setBgUrl("http://center.pay.sogou.com/pay-web/notify/ali/pay/testBgUrl");
        params.setBankCardType("");
        Map map = BeanUtil.beanToMap(params);
        map.put("sign", MD5Util.MD5Encode(params.getVersion()+"&"+params.getOrderId()+"&"+params.getOrderAmount()
                +"&"+params.getOrderTime()+"&"+params.getAppId()+"&"+params.getSignType()+"&"+params.getAccessPlatform()
                +"862653da5865293b1ec8cc","utf-8"));
        testGet(url, map);
    }
    @Test
    public void testPayWebs() {
        ResultMap result = ResultMap.build();
        result.addItem("refundStatus", "sss");
        System.out.println(JSONObject.toJSONString(result));

    }
}
