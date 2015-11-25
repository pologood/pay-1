package com.sogou.pay.notify;

import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.common.utils.httpclient.MerchantHttpClient;
import com.sogou.pay.common.utils.httpclient.MerchantResponse;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qibaichao on 2015/4/9.
 */
public class MerchantHttpClientTest {


    @Test
    public void doPost() {
        String path = "http://zhongyi.sogou.com/tcm_offline/?op=mszy_pay_feedback";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("signType", "0");
        paramMap.put("orderMoney", "0.01");
        paramMap.put("orderId", "123456789004");
        paramMap.put("isSuccess", "T");
        paramMap.put("sign", "DB54A935239A21CE613E047CEA553E3D");
        paramMap.put("payId", "ZFD20151030155510140001");
        paramMap.put("tradeStatus", "TRADE_FINISHED");
        paramMap.put("successTime", "20151030153655");
        paramMap.put("appId", "5000");
        MerchantResponse merchantResponse = MerchantHttpClient.getInstance().doPost(path, paramMap);
        System.out.println(JsonUtil.beanToJson(merchantResponse));

    }

    @Test
    public void doGet() {
        String path = "http://10.129.204.35:8080/sylla/hackathon2/betting/order/notified";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("aa", "aa");
        MerchantResponse merchantResponse = MerchantHttpClient.getInstance().doGet(path, paramMap);
        System.out.println(JsonUtil.beanToJson(merchantResponse));

    }


}
