package com.sogou.pay.common.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeanUtil {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    public static void setBeanProperty(Object object, String name, Object value) {
        try {
            BeanUtils.setProperty(object, name, value);
        } catch (IllegalAccessException e) {
            logger.error("Put Value To ResultDO IllegalAccessException! ParamName:" + name + ", Value:" + value, e);
        } catch (InvocationTargetException e) {
            logger.error("Put Value To ResultDO InvocationTargetException! ParamName:" + name + ", Value:" + value, e);
        }
    }

    public static String getBeanSimpleProperty(Object object, String name) {
        String value = "";
        try {
            value = BeanUtils.getSimpleProperty(object, name);
        } catch (IllegalAccessException e) {
            logger.error("Get Value From DO IllegalAccessException! ParamName:" + name, e);
        } catch (InvocationTargetException e) {
            logger.error("Get Value From DO InvocationTargetException! ParamName:" + name, e);
        } catch (NoSuchMethodException e) {
            logger.error("Get Value From DO NoSuchMethodException! ParamName:" + name, e);
        }
        return value;
    }

    /**
     * Bean转换为Map，去除key和value为null的项
     * @param object Bean对象
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMapNotNull(Object object) {
        Map<String, Object> map = beanToMap(object);
        if (map != null) {
            map.remove(null);
            List<String> keys = new ArrayList(map.keySet());
            for (String key : keys) {
                if (map.get(key) == null) {
                    map.remove(key);
                }
            }
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getBeanPropertiesNotBlank(Object object) {
        Map<String, Object> map = beanToMap(object);
        if (map != null) {
            List<String> keys = new ArrayList<>(map.keySet());
            for (String key : keys) {
                if (StringUtil.isBlank(map.get(key).toString())) {
                    map.remove(key);
                }
            }
        }
        return map;
    }

    /**
     * 未使用BeanUtils.describe()，因该方法在映射List时只显示第一个元素
     *
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMap(Object object) {
        return beanToType(object, Map.class);
    }

    private static <T> T beanToType(Object object, Class<T> clazz) {
        /*map = BeanUtils.describe(object);
        map.remove("class");
        map.remove(null);*/
        return JacksonJsonUtil.getMapper().convertValue(object, clazz);
    }
}
