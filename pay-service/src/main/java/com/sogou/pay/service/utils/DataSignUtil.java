package com.sogou.pay.service.utils;

import org.apache.commons.lang.StringUtils;

import com.sogou.pay.common.utils.MD5Util;

/**
 * 业务平台签名、验证签名类
 */
public class DataSignUtil {

    /**
     * 基础签名方法
     *
     * @param str
     * @param signType 0:MD5 1:SHA
     * @return string
     */
    public static String sign(String str, String signType) {
        String signData = null;
        if (StringUtils.isBlank(signType) || "0".equals(signType))
            signData = MD5Util.sign(str, "MD5");
        else
            signData = MD5Util.sign(str, "SHA");
        return signData;
    }

    /**
     * 基础验证签名方法
     *
     * @param dataString 明文
     * @param signString 之前已经做好的签名
     */
    public static Boolean verifySign(String dataString, String signType, String signString) {
        return signString.equals(sign(dataString, signType));
    }


}
