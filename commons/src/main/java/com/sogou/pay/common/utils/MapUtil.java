package com.sogou.pay.common.utils;

import com.google.common.collect.Lists;

import com.sogou.pay.common.annotation.MapField;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hujunfei Date: 15-1-7 Time: 下午12:32
 */
public final class MapUtil {

    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    /**
     * 删除Key或Value为null的键值对
     */
    public static Map dropNulls(Map map) {
        if (isEmpty(map)) {
            return map;
        }
        map.remove(null);
        List keys = Lists.newArrayList(map.keySet());
        for (Object key : keys) {
            if (map.get(key) == null) {
                map.remove(key);
            }
        }
        return map;
    }

    /**
     * 删除Value为null的键值对
     */
    public static Map dropNullValues(Map map) {
        if (isEmpty(map)) {
            return map;
        }
        List keys = Lists.newArrayList(map.keySet());
        for (Object key : keys) {
            if (map.get(key) == null) {
                map.remove(key);
            }
        }
        return map;
    }

    /**
     * 检查List中的键是否都在Map中存在，Map为空返回false，List为空返回true
     */
    public static boolean checkAllExist(Map map, List keys) {
        if (map == null) {
            return false;
        }
        if (keys == null) {
            return true;
        }
        for (Object key : keys) {
            if (map.get(key) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查List中的键是否都在Map中存在，Map为空返回false，List为空返回true
     */
    public static boolean checkAllExist(Map map, Object[] keys) {
        if (map == null) {
            return false;
        }
        if (keys == null) {
            return true;
        }
        for (Object key : keys) {
            if (map.get(key) == null) {
                return false;
            }
        }
        return true;
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
    public static <T> T toBean(Map map, Class<T> clazz) {
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

            /*Map beanMap = BeanUtil.beanToMap(t);
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
