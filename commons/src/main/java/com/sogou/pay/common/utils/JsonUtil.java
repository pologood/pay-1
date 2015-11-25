package com.sogou.pay.common.utils;

import com.alibaba.fastjson.JSON;

/**
 * Created by hujunfei Date: 14-12-30 Time: 下午1:37
 */
public class JsonUtil {

    /**
     * @param value
     * @param classType
     * @param <T>
     * @return
     */
    public static <T> T jsonToBean(String value, Class<T> classType) {
        return JSON.parseObject(value, classType);
    }

    public static PMap jsonToPMap(String value, PMap map) {
        return JSON.parseObject(value, PMap.class);
    }

    /**
     * @param obj
     * @return
     */
    public static String beanToJson(Object obj) {
        return JSON.toJSONString(obj);
    }

}
