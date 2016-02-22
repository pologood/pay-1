package com.sogou.pay.thirdpay;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.Model.RefundResult;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
//import com.sogou.pay.thirdpay.api.QueryApi;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/2 16:31
 */
public class QueryApiTest extends BaseTest {

/*    @Autowired
    private QueryApi queryApi;
    *//**
     * 财付通查询订单---测试成功
     *//*
    @Test
    public void testTenAccountPay() {
        Map refundMap = new HashMap();
        refundMap.put("refundStatus", "333");
        refundMap.put("message","成功");
        refundMap.put("status", "SUCCESS");
        ResultMap SS = ResultMap.build();
        Object sss= SS.getData().get("assdf");

        RefundResult refundModel =new RefundResult();
        refundModel.setStatus(ResultStatus.REFUND_PARAM_ERROR.toString());
        refundModel.setMessage(ResultStatus.REFUND_PARAM_ERROR.getMessage());
        System.out.print("refundRefund" + JSONObject.toJSONString(refundModel));
    }

    *//**
     * 支付宝查询订单--测试成功
     *//*
    @Test
    public void testAliPayQuery() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "ALIPAY");
        pmap.put("merchantNo", "2088811923135335");
        pmap.put("queryUrl", "https://mapi.alipay.com/gateway.do");
        pmap.put("md5securityKey", "20w5obaxam7keamcuzk7cfiu46j4htg0");
        pmap.put("queryReqTime", new Date());
        pmap.put("serialNumber", "ZF2015071511091147150011");
        result = queryApi.queryOrder(pmap);
        System.out.print("result" + result);
    }

    *//**
     * 微信查询订单--测试成功
     *//*
    @Test
    public void testWenPayQuery() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "WECHAT");
        pmap.put("merchantNo", "1234469202");
        pmap.put("sellerEmail", "wx14cdf1737b024a16");
        pmap.put("queryUrl", "https://api.mch.weixin.qq.com/pay/orderquery");
        pmap.put("md5securityKey", "1hu8aa7dbnldi012y984klo28uom5r42");
        pmap.put("queryReqTime", new Date());
        pmap.put("serialNumber", "ZF20150407142503322001");
        pmap.put("agencySerialNumber", "gph201222222");
        pmap.put("payTime", "20150302165723");
        result = queryApi.queryOrder(pmap);
        System.out.print("result" + result);
    }

    *//**
     * 快钱查询订单
     *//*
    @Test
    public void testBillPayQuery() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("agencyCode", "99BILL");
        pmap.put("merchantNo", "10012138842");
        pmap.put("sellerEmail", "wx14cdf1737b024a16");
        pmap.put("queryUrl", "https://www.99bill.com/apipay/services/gatewayOrderQuery?wsdl");
        pmap.put("md5securityKey", "1hu8aa7dbnldi012y984klo28uom5r42");
        pmap.put("queryReqTime", new Date());
        pmap.put("serialNumber", "20130111170538");
        pmap.put("agencySerialNumber", "gph201222222");
        pmap.put("payTime", "20150302165723");
        pmap.put("md5securityKey", "5UHQX2G65W4ECF5G");
        result = queryApi.queryOrder(pmap);
        System.out.print("result" + result);
    }*/
}
