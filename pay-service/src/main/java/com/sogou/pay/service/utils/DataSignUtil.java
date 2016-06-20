package com.sogou.pay.service.utils;

import com.sogou.pay.common.utils.DigestUtil;
import org.apache.commons.lang.StringUtils;

/**
 * 业务平台签名、验证签名类
 */
public class DataSignUtil {
    public static String sign(String str, String signType) {
        String signData = null;
        if (StringUtils.isBlank(signType) || "0".equals(signType))
            signData = DigestUtil.MD5Encode(str, null);
        else
            signData = DigestUtil.SHAEncode(str, null);
        return signData;
    }
}
