package com.sogou.pay.common.utils;

import com.sogou.pay.common.BaseTest;

import org.junit.Test;

/**
 * Created by hujunfei Date: 14-12-30 Time: 下午3:19
 */
public class JsonUtilTest extends BaseTest {

    @Test
    public void testJsonToBean() {
        // JsonUtil.jsonToBean()
        String json = "{\"arr\":[\"Hello\",\"World\",\"!\"],\"id\":10,\"list\":[\"list1\",\"list2\"],\"map\":{\"key1\":\"value1\",\"key2\":\"value2\"},\"name\":\"Test\"}";
        TestBean bean = JsonUtil.jsonToBean(json, TestBean.class);
        System.out.println(bean.getMap());
    }

    @Test
    public void testBeanToJson() {
        System.out.println(JsonUtil.beanToJson(null));
        System.out.println(JsonUtil.beanToJson(getBean()));
    }
}
