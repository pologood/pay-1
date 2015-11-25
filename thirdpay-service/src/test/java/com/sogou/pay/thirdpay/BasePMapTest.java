package com.sogou.pay.thirdpay;

import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.thirdpay.biz.utils.Utils;

import org.junit.Test;

import java.util.Date;
import java.util.Map;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/28 15:41
 */
public class BasePMapTest extends BaseTest {
    @Test
    public void testSweepYardsPreparePayInfo() {
        PMap pmap = new PMap();
        pmap.put("time","20150302165723");
        Date SS = pmap.getDate("time");
        String refundDate = Utils.dateToString(SS, "yyyy-MM-dd hh:mm:ss");
        System.out.print("result"+SS);
    }
}
