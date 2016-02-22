package com.sogou.pay.thirdpay;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;
//import com.sogou.pay.thirdpay.api.RefundApi;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/2 16:31
 */
public class RefundApiTest extends BaseTest {

/*    @Autowired
    private RefundApi refundApi;

    *//**
     * 财付通退款--测试通过
     *//*
    @Test
    public void testTenAccountPay1() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "TENPAY");
        pmap.put("merchantNo", "1234639901");
        pmap.put("refundUrl", "https://mch.tenpay.com/refundapi/gateway/refund.xml");
        pmap.put("refundNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("md5securityKey", "dcfe3c50f2fa354351333aa9622b9f95");
        pmap.put("publicCertFilePath", "/pay_key/tenpay_keji/cacert.pem");
        pmap.put("privateCertFilePath", "/pay_key/tenpay_keji/1234639901_20150319170653.pfx");
        pmap.put("refundSerialNumber", "20150519162426636001");
        pmap.put("refundReqTime", new Date());
        pmap.put("serialNumber", "ZF20150519162229644001");
        pmap.put("agencySerialNumber", "1234639901201505190781101813");
        pmap.put("refundAmount", "0.01");
        pmap.put("totalAmount", "0.01");
        result = refundApi.refundOrder(pmap);
        System.out.print("result" + result);
    }

    *//**
     * 财付通退款--测试通过
     *//*
    @Test
    public void testTenAccountPay2() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "TENPAY");
        pmap.put("merchantNo", "1234274801");
        pmap.put("refundUrl", "https://mch.tenpay.com/refundapi/gateway/refund.xml");
        pmap.put("refundNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("md5securityKey", "6055a74d5033faddd45b94e108a51386");
        pmap.put("publicCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("privateCertFilePath", "/pay_key/tenpay/1234274801_20150317144755.pfx");
        pmap.put("refundSerialNumber", "TK201503101745");
        pmap.put("refundReqTime", new Date());
        pmap.put("serialNumber", "ZFD201503101812");
        pmap.put("agencySerialNumber", "1900000109201503100532241394");
        pmap.put("refundAmount", "0.01");
        pmap.put("totalAmount", "0.01");
        result = refundApi.refundOrder(pmap);
        System.out.print("result" + result);
    }

    *//**
     * 支付宝退款--测试通过
     *//*
    @Test
    public void testAliPayRefund() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "ALIPA1Y");
        pmap.put("merchantNo", "2088811923251859");
        pmap.put("refundUrl", "https://mapi.alipay.com/gateway.do");
        pmap.put("refundNotifyUrl", "http://center.pay.sogou.com/pay-web/notify/refund/alipay/30");
        pmap.put("md5securityKey", "e4jccbgm2c63o47xlgn129107jgg0wiv");
        pmap.put("publicCertFilePath", "/pay_key/alipay_keji/alipay_public_key.pem");
        pmap.put("privateCertFilePath", "/pay_key/alipay_keji/rsa_private_key.pem");
        pmap.put("refundSerialNumber", "201508271126592222221");
        pmap.put("refundReqTime", new Date());
        pmap.put("serialNumber", "ZF20150827142847596001");
        pmap.put("agencySerialNumber", "2015082700001000500061281872");
        pmap.put("refundAmount", "0.01");
        pmap.put("totalAmount", "0.01");
        result = refundApi.refundOrder(pmap);
        System.out.print("result" + result);
    }

    *//**
     * 微信退款
     *//*
    @Test
    public void testWenPayRefund() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "WECHAT");
        pmap.put("merchantNo", "1234469202");
        pmap.put("sellerEmail", "wx14cdf1737b024a16");
        pmap.put("refundUrl", "https://api.mch.weixin.qq.com/secapi/pay/refund");
        pmap.put("refundNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("md5securityKey", "1hu8aa7dbnldi012y984klo28uom5r42");
        pmap.put("publicCertFilePath", "/pay_key/wechat/rootca.pem");
        pmap.put("privateCertFilePath", "/pay_key/wechat/apiclient_cert.p12");
        pmap.put("refundSerialNumber", "2002343454564sss56");
        pmap.put("refundReqTime", new Date());
        pmap.put("serialNumber", "ZF20150407142503322001");
        pmap.put("agencySerialNumber", "1008450318201504070050163316");
        pmap.put("refundAmount", "0.01");
        pmap.put("totalAmount", "0.01");
        result = refundApi.refundOrder(pmap);
        System.out.print("result" + result);
    }


    *//**
     * 微信退款--科技账号
     *//*
    @Test
    public void testKejiWenPayRefund() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "WECHAT");
        pmap.put("merchantNo", "1246690601");
        pmap.put("sellerEmail", "wx9a3d0ebef06f62b8");
        pmap.put("refundUrl", "https://api.mch.weixin.qq.com/secapi/pay/refund");
        pmap.put("refundNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("md5securityKey", "2idEk034kdui3WESr8Ef8wW9edfQw3s2");
        pmap.put("publicCertFilePath", "/pay_key/wechat_keji/rootca.pem");
        pmap.put("privateCertFilePath", "/pay_key/wechat_keji/apiclient_cert.p12");
        pmap.put("refundSerialNumber", "2002343454564sss5617");
        pmap.put("refundReqTime", new Date());
        pmap.put("serialNumber", "ZF20150629144252925001");
        pmap.put("agencySerialNumber", "1008450004201506290321455644");
        pmap.put("refundAmount", "0.01");
        pmap.put("totalAmount", "0.01");
        result = refundApi.refundOrder(pmap);
        System.out.print("result" + result);
    }


    *//**
     * 快钱退款
     *//*
    @Test
    public void testBillPayRefund() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "99BILL");
        pmap.put("merchantNo", "10012138842");
        pmap.put("sellerEmail", "wx14cdf1737b024a16");
        pmap.put("refundUrl", "https://sandbox.99bill.com/webapp/receiveDrawbackAction.do");
        pmap.put("refundNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("md5securityKey", "U9HSFFJ9UDEKY7T6");
        pmap.put("refundSerialNumber", "20150629170626");
        pmap.put("publicCertFilePath", "/pay_key/wechat/rootca.pem");
        pmap.put("privateCertFilePath", "/pay_key/wechat/apiclient_cert.p12");
        pmap.put("refundReqTime", "20150629170626");
        pmap.put("serialNumber", "20130111170538");
        pmap.put("agencySerialNumber", "1008450318201504070050163316");
        pmap.put("refundAmount", "10");
        pmap.put("totalAmount", "10");
        result = refundApi.refundOrder(pmap);
        System.out.print("result" + result);
    }*/
}
