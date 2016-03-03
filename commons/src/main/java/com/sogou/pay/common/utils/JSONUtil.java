package com.sogou.pay.common.utils;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.common.types.PMap;

import java.util.Map;

/**
 * Created by hujunfei Date: 14-12-30 Time: 下午1:37
 */
public class JSONUtil {

    public static PMap jsonToPMap(String value, PMap map) {
        return JSON.parseObject(value, PMap.class);
    }

    public static Map JSON2Map(String json){
        return JSON.parseObject(json, Map.class);
    }
    public static PMap JSON2PMap(String json){
        return JSON.parseObject(json, PMap.class);
    }

    public static String Map2JSON(Map map){
        return JSON.toJSONString(map);
    }

    public static <T> T JSON2Bean(String json, Class<T> clazz){
        return JSON.parseObject(json, clazz);
    }


    public static String Bean2JSON(Object bean){
        return JSON.toJSONString(bean);
    }
}
