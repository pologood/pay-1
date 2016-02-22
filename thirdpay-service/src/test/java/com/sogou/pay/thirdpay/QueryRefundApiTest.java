package com.sogou.pay.thirdpay;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;
//import com.sogou.pay.thirdpay.api.QueryRefundApi;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/2 16:31
 */
public class QueryRefundApiTest extends BaseTest {

/*    @Autowired
    private QueryRefundApi queryRefundApi;

    *//**
     * 支付宝查询订单退款
     *//*
    @Test
    public void testAliPayQuery() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "ALIPAY1");
        pmap.put("merchantNo", "20888119231353135");
        pmap.put("queryRefundUrl", "https://mapi.alipay.com/gateway.do");
        pmap.put("md5securityKey", "20w5obaxam7keamcuzk7cfiu46j4htg0");
        pmap.put("out_refund_no", "20150409110015s089001");
        result = queryRefundApi.queryRefund(pmap);
        System.out.print("result" + result);
    }

    *//**
     * 财付通查询订单退款--测试成功
     *//*
    @Test
    public void testTenAccountPay() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "TENPAY");
        pmap.put("merchantNo", "1234639901");
        pmap.put("queryRefundUrl", "https://gw.tenpay.com/gateway/normalrefundquery.xml");
        pmap.put("md5securityKey", "sdf23er4edfrgh5634sdf09qw23sdsd3");
        pmap.put("out_refund_no", "20151104152444140001");
        result = queryRefundApi.queryRefund(pmap);
        System.out.print("result" + result);
    }

    *//**
     * 微信查询订单退款--测试成功
     *//*
    @Test
    public void testWenPayQuery() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("merchantNo", "1234469202");
        pmap.put("sellerEmail", "wx14cdf1737b024a16");
        pmap.put("agencyCode", "WECHAT");
        pmap.put("queryRefundUrl", "https://api.mch.weixin.qq.com/pay/refundquery");
        pmap.put("md5securityKey", "1hu8aa7dbnldi012y984klo28uom5r42");
        pmap.put("out_refund_no", "TK20150408100854439001");
        pmap.put("refund_time", "20130424000000");
        result = queryRefundApi.queryRefund(pmap);
        System.out.print("result" + result);
    }

    *//**
     * 快钱查询订单退款
     *//*
    @Test
    public void testBillPayQuery() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("merchantNo", "10012143357");
        pmap.put("agencyCode", "99BILL");
        pmap.put("queryRefundUrl", "https://api.mch.weixin.qq.com/pay/refundquery");
        pmap.put("md5securityKey", "4IQLTUNT365Z6N4K");
        pmap.put("out_refund_no", "20130425171143");
        pmap.put("refund_time", "20130424000000");
        result = queryRefundApi.queryRefund(pmap);
        System.out.print("result" + result);
    }*/
}
