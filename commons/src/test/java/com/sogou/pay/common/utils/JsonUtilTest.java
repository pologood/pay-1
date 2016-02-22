package com.sogou.pay.common.utils;

import com.sogou.pay.common.BaseTest;

import org.junit.Test;

/**
 * Created by hujunfei Date: 14-12-30 Time: 下午3:19
 */
public class JsonUtilTest extends BaseTest {

    @Test
    public void testJsonToBean() {
        // JsonUtil.JSON2Bean()
        String json = "{\"arr\":[\"Hello\",\"World\",\"!\"],\"id\":10,\"list\":[\"list1\",\"list2\"],\"map\":{\"key1\":\"value1\",\"key2\":\"value2\"},\"name\":\"Test\"}";
        TestBean bean = JSONUtil.JSON2Bean(json, TestBean.class);
        System.out.println(bean.getMap());
    }

    @Test
    public void testBeanToJson() {
        System.out.println(JSONUtil.Bean2JSON(null));
        System.out.println(JSONUtil.Bean2JSON(getBean()));
    }
}
