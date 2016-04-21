package com.sogou.pay.service.payment.manager;

import com.sogou.pay.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.payment.PayManager;

public class PayManagerTest extends BaseTest {
    @Autowired
    private PayManager payManager;
    
    public void confirmPayTest(){
        PMap map = new PMap();
        map.put("appId", "1999");
        map.put("accountId", "");
        map.put("channelCode", "ABC");
        map.put("pageUrl", "http://pageurl.sogou.com");
        map.put("signType", "0");
        map.put("orderTime", "20150309150700");
        map.put("version", "v1.0");
        map.put("bankId", "ABC");
        map.put("belongCompany", "ZFD20150312140651149001");
        map.put("sign", "50D29CF5740C27E9419CB9BDA426B3A6");
        map.put("productDesc", "测试商品");
        map.put("bgUrl", "http://bgurl.sogou.com");
        map.put("orderAmount", "50.01");
        map.put("productNum", "1");
        map.put("appData", null);
        map.put("productName", "测试商品");
        map.put("orderId", "ORDER22222222");
        map.put("bankCardType", "1");
        payManager.confirmPay(map);
    }
    
}
