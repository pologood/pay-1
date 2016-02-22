package com.sogou.pay.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogou.pay.common.annotation.MapField;
import com.sogou.pay.common.types.PMap;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class BeanUtil {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getMapper() {
        return objectMapper;
    }

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
     *
     * @param object Bean对象
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> Bean2MapNotNull(Object object) {
        Map<String, Object> map = Bean2Map(object);
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
    public static Map<String, Object> Bean2MapNotBlank(Object object) {
        Map<String, Object> map = Bean2Map(object);
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


    public static <T> T Map2Bean(Map map, Class<T> clazz) {
        try {
            T bean = clazz.newInstance();
            BeanUtils.populate(bean, map);
            return bean;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Map Bean2Map(Object bean){
        return objectMapper.convertValue(bean, Map.class);
    }

    public static PMap Bean2PMap(Object bean){
        return objectMapper.convertValue(bean, PMap.class);
    }

    /**
     * 将Map中的键值对映射为Bean，通过注解MapField来标示Bean成员变量对应Map中的key <p/><i color="red">不支持映射从父类继承的私有成员变量</i>
     * <br/>TODO: 修改为支持从父类继承私有成员变量或者根据setter/getter来设置
     *
     * @param map   源Map
     * @param clazz 返回Bean类型
     * @return 映射后的Bean实例
     * @throws java.lang.IllegalArgumentException 存在无法转换的数据类型
     * @see com.sogou.pay.common.annotation.MapField
     */
    public static <T> T MaptoBean(Map map, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            MapUtil.dropNulls(map);

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // field.setAccessible(true);
                MapField mapField = field.getAnnotation(MapField.class);
                String mapKey = (mapField != null ? mapField.key() : field.getName());
                BeanUtil.setBeanProperty(t, field.getName(), map.get(mapKey));
            }

            /*Map beanMap = BeanUtil.Bean2Map(t);
            for (Object key : beanMap.keySet()) {
                Field field;
                try {
                    field = clazz.getField(key.toString());
                } catch (NoSuchFieldException nsfe) {
                    field = clazz.getDeclaredField(key.toString());
                }
                // field.setAccessible(true);
                MapField mapField = field.getAnnotation(MapField.class);
                String mapKey = (mapField != null ? mapField.key() : field.getName());
                BeanUtil.setBeanProperty(t, field.getName(), map.get(mapKey));
            }*/
            return t;
        } catch (Exception e) {
            throw new IllegalArgumentException("Convert Map To " + clazz.getSimpleName() + " Error: " + map, e);
        }
    }

}
