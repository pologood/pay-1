package com.sogou.pay.web.utils;

import com.sogou.pay.web.BaseTest;

import org.junit.Test;

/**
 * Created by gaopenghui on 2015/3/16.
 */
public class WechatCodeUtilTest extends BaseTest {

    @Test
    public void testValidate() {
        String ss = "weixin://wxpay/bizpayurl?pr=NWNoR08";
        try {
            String ssss = WechatCodeUtil.genWechatCode(ss);
            System.out.println(ssss);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
