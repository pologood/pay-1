package com.sogou.pay.common.http.utils;

import com.sogou.pay.common.BaseTest;
import com.sogou.pay.common.utils.BeanUtil;

import org.junit.Test;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hujunfei Date: 15-1-7 Time: 下午3:46
 */
public class HttpUtilTest extends BaseTest {

    @Test
    public void packHttpsGetUrl() {
        try {

//            String s1 = HttpUtil.sendPost("http://zhongyi.sogou.com/tcm_offline/?op=mszy_pay_feedback&", "signType=0&orderMoney=0.01&orderId=123456789004&isSuccess=T&sign=DB54A935239A21CE613E047CEA553E3D&payId=ZFD20151030155510140001&tradeStatus=TRADE_FINISHED&successTime=20151030153655&appId=5000");
//            String s2 = HttpUtil.sendPost("http://zhongyi.sogou.com/tcm_offline/?op=mszy_pay_feedback&", "orderId=123456789002&successTime=20151030145315&payId=ZFD20151030141525140001&tradeStatus=TRADE_FINISHED&appId=5000&orderMoney=0.01&sign=0AE1A44D4CC0EFFDD2E7CF5ECB1F44BB&signType=0&isSuccess=T");
//            System.out.println(s1);
//            System.out.println(s2);
//            String s2 = HttpUtil.sendPost("http://center.pay.sogou.com/notify/ali/pay/testBgUrl", "sign=aaa&payId=222&refundSuccessTime=2015-10-30+19%3A00%3A57&refundAmount=1&refundStatus=refund_success&signType=ddd&orderId=111");
//            String s2 = HttpUtil.sendPost("http://10.129.204.35:8080/sylla/hackathon2/betting/order/notified", "sign=aaa&payId=222&refundSuccessTime=2015-10-30+19%3A00%3A57&refundAmount=1&refundStatus=refund_success&signType=ddd&orderId=111");
//            System.out.println(s2);

            Map map  = new HashMap<String,String>();
            map.put("aa","aa");
            System.out.println(map.remove("aa"));
        } catch (IllegalArgumentException iae) {
            System.out.println(iae.getMessage());
        }
    }
}
