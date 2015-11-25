package com.sogou.pay.common.utils;

import java.util.Map;

/**
 * Created by hujunfei Date: 15-1-9 Time: 下午5:41
 */
public class PMapUtil {

    /**
     * 借助MapField注解转换PMap为Bean
     *
     * @param pMap  待转换的PMap
     * @param clazz Bean类型，不能转换父类的成员变量
     * @return 类型为clazz的Bean实例
     * @see com.sogou.pay.common.annotation.MapField
     */
    public static <T> T toBean(PMap pMap, Class<T> clazz) {
        return MapUtil.toBean(pMap, clazz);
    }


    public static PMap fromBean(Object obj) {
        return JacksonJsonUtil.getMapper().convertValue(obj, PMap.class);
    }

    public static PMap fromPMap(PMap pMap, Map mappings) {
        try {
            MapUtil.dropNulls(mappings);
            PMap result = new PMap(pMap);
            for (Object key : mappings.keySet()) {
                result.put(key, pMap.get(mappings.get(key)));
            }
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Convert PMap To PMap Error: " + pMap + ", " + mappings, e);
        }
    }
}
