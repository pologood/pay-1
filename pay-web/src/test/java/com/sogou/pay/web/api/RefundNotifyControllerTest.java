package com.sogou.pay.web.api;

import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.api.PayController;
import com.sogou.pay.web.form.notify.AliRefundNotifyParams;
import com.sogou.pay.web.form.notify.TenRefundNotifyParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
/**
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/16 11:11
 */
public class RefundNotifyControllerTest extends BaseTest {
    @Autowired
    PayController controller;
    
    
    @Test
    public void testAliRefundNotify() {
        String url = "/notify/refund/alipay/30";
        AliRefundNotifyParams params = new AliRefundNotifyParams();
        params.setBatch_no("20151030172229140001");
        params.setNotify_id("dc74743ea959b5c33adf659ff00e9ffk8s");
        params.setNotify_time("2015-10-30 17:22:33");
        params.setResult_details("2015103021001004410037063246^0.01^SUCCESS");
        params.setSign("7b9843bed433a4c03c819dcc866a4a49");
        params.setNotify_type("batch_refund_notify");
        params.setSign_type("MD5");
        params.setSuccess_num("1");
        Map map = BeanUtil.Bean2Map(params);
        testGet(url, map);
    }

    @Test
    public void testTenRefundNotify() {
        String url = "/notify/refund/tenpay/2";
        TenRefundNotifyParams params = new TenRefundNotifyParams();
        params.setOut_refund_no("20150428164035168001");
        params.setOut_trade_no("ZF20150428163311426001");
        params.setPartner("1234274801");
        params.setRefund_channel("0");
        params.setRefund_fee("1");
        params.setRefund_status("4");
        params.setRefund_id("11112342748012015040961705s70");
        params.setSign("2BF7365A6A0B7B2A782E14C881574FAB");
        params.setTransaction_id("1234274801201504280703349053");
        Map map = BeanUtil.Bean2Map(params);
        testGet(url, map);
    }
    
}
