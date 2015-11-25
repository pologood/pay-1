package com.sogou.pay.service.payment;

import com.sogou.pay.common.http.model.RequestModel;
import com.sogou.pay.service.BaseTest;
import com.sogou.pay.service.connect.HttpClientService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hujunfei Date: 14-12-31 Time: 下午6:52
 */
public class HttpClientServiceTest extends BaseTest {
    @Autowired
    HttpClientService httpClientService;

    @Test
    public void testExecuteStr() {
        RequestModel requestModel = new RequestModel("https://account.sogou.com");
        System.out.println(httpClientService.executeStr(requestModel));
    }
}
