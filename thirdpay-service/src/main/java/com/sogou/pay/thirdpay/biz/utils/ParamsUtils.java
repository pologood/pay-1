package com.sogou.pay.thirdpay.biz.utils;

import java.net.URLDecoder;
import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName ParamsUtils
 * @Date 2015年2月16日
 * @Description:工具类
 */
public class ParamsUtils {

    public static String toString(Map<String, String> params) {
        if (null == params) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String key : params.keySet()) {
            sb.append(key).append("=").append(params.get(key)).append(", ");
        }
        String str = sb.toString();
        if (params.size() > 0) {
            int lastIndex = str.lastIndexOf(",");
            str = str.substring(0, lastIndex);
            str = str + "]";
            return str;
        }

        return "[]";
    }

    public static void decode(Map<String, String> params, String code) {

        for (String key : params.keySet()) {
            String value = params.get(key);
            try {
                value = URLDecoder.decode(value, code);
            } catch (Exception e) {
                e.printStackTrace();
            }
            params.put(key, value);
        }

    }
}
