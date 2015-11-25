package com.sogou.pay.common.utils;

import com.sogou.pay.common.BaseTest;

import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by hujunfei Date: 15-1-9 Time: 上午11:44
 */
public class ConvertUtilTest extends BaseTest {

    @Test
    public void testConvert() {
        TestBean bean = getBean();
        convertInt(bean);
        convertInt(bean.getMap());
        convertInt(bean.getList());
        convertInt(bean.getName());
        convertInt(bean.getId());
        convertInt(null);
        convertInt("100000000000000000000");
        convertInt("-100000000000000000000");
        convertInt("100000000");
        convertInt("-100000000");
        convertInt(new BigInteger("100000000000000000000"));
        convertInt(new BigInteger("-100000000000000000000"));
        convertInt(new BigInteger("100000000"));
        convertInt(new BigInteger("-100000000"));
        convertInt(1000000000);
        convertInt(-1000000000);
        convertInt(10000000000000000L);
        convertInt(-10000000000000000L);
    }

    private void convertInt(Object obj) {
        try {
            System.out.println(ConvertUtil.toInt(obj));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
