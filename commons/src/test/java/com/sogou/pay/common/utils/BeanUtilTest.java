package com.sogou.pay.common.utils;

import com.sogou.pay.common.BaseTest;

import org.junit.Test;

import java.util.Map;

/**
 * Created by hujunfei Date: 15-1-4 Time: 下午5:18
 */
public class BeanUtilTest extends BaseTest {

    @Test
    public void testBeanToMap() {
        TestBean bean = getBean();
        Map map = BeanUtil.beanToMap(bean);
        System.out.println(map.toString());
        assertTrue(map.containsKey("nickname"));
        assertTrue(map.get("nickname") == null);
    }

    @Test
    public void testBeanToMapNotNull() {
        TestBean bean = getBean();
        Map map = BeanUtil.beanToMapNotNull(bean);
        System.out.println(map.toString());
        System.out.println(map.get("list").getClass());
        assertFalse(map.containsKey("nickname"));
        assertTrue(map.get("nickname") == null);
    }
}
