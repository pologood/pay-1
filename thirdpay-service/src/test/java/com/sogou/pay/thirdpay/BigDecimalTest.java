package com.sogou.pay.thirdpay;

import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.thirdpay.biz.utils.Utils;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * 
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/3 13:19
 */
public class BigDecimalTest extends BaseTest {
    @Test
         public void testSweepYardsPreparePayInfo() {
        PMap params = new PMap();
        params.put("orderAmount","0");
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        int sss =oAmount.compareTo(new BigDecimal(0));

        System.out.print("result"+oAmount);
    }

    @Test
    public void test2() {
        PMap params = new PMap();
        params.put("orderAmount","0.2252222");
        BigDecimal oAmount = Utils.parseFromYuan(params.getString("orderAmount"));
        int sss =oAmount.compareTo(new BigDecimal(0));
        BigDecimal oAmounts = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = Utils.fenParseFromYuan(params.getString("orderAmount"));
        System.out.print("result"+oAmount);
    }

}
