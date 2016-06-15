package com.sogou.pay.service.utils;

import org.apache.commons.lang.StringUtils;

import com.sogou.pay.common.utils.MD5Util;

/**
 * 业务平台签名、验证签名类
 */
public class DataSignUtil {
    public static String sign(String str, String signType) {
        String signData = null;
        if (StringUtils.isBlank(signType) || "0".equals(signType))
            signData = MD5Util.MD5Encode(str, null);
        else
            signData = MD5Util.SHAEncode(str, null);
        return signData;
    }
}
