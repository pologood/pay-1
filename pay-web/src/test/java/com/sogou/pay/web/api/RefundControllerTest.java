package com.sogou.pay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.MD5Util;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.PayController;
import com.sogou.pay.web.form.PayParams;
import com.sogou.pay.web.form.RefundParams;
import com.sogou.pay.web.form.notify.AliRefundNotifyParams;
import com.sogou.pay.web.form.notify.TenRefundNotifyParams;
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
        params.setAppId("1999");
        params.setBgUrl("http://127.0.0.1:8080/notify/ali/pay/testBgUrl");
        params.setSignType("0");
        params.setOrderId("OD20160114112948996");
        //params.setRefundAmount("");
        Map map = BeanUtil.beanToMap(params);
        map.put("sign", JSONObject.parse(controller.signData(map, null)));
        testGet(url, map);
    }
}
