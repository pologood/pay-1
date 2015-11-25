package com.sogou.pay.web.api;

import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.form.QueryRefundParams;
import org.junit.Test;

import java.util.Map;

/**
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/16 11:11
 */
public class QueryRefundControllerTest extends BaseTest {

    @Test
    public void testQUERYrefundPayWeb() {
        String url = "/queryRefund/doQuery";
        QueryRefundParams params = new QueryRefundParams();
        params.setOrderId("OD20150410040237635");
        params.setSign("022c1a6da5a890ea70658afc22424d93");
        params.setSignType("MD5");
        params.setAppId("1999");
        Map map = BeanUtil.beanToMap(params);
        testGet(url, map);
    }

}
