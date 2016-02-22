/**
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/1/9 11:24
 */
package com.sogou.pay.thirdpay.service.Tenpay;

import com.sogou.pay.common.utils.MD5Util;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class TenpayUtils {

    public static final String CHARSET_UTF_8 = "UTF-8";


    /**
     * 金额格式化,以分为单位,精确到分,之后四舍五入.
     */
    public static String amountToFen(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        return amount.multiply(new BigDecimal(100))
                .setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 金额格式化,以元为单位,精确到分,之后四舍五入.
     */
    public static String amountToYuan(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }


    /**
     * 字符串到金额类型转换,以分为单位,精确到分,之后四舍五入.
     */
    public static BigDecimal parseFromFen(String amount) {
        return new BigDecimal(amount).divide(new BigDecimal(100)).setScale(2,
                BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 字符串到金额类型转换,以元为单位,精确到分,之后四舍五入.
     */
    public static BigDecimal parseFromYuan(String amount) {
        return new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 以元为单位的字符串金额到以分为单位金额字符串类型转换,精确到分,之后四舍五入.
     */
    public static String fenParseFromYuan(String amount) {
        BigDecimal amountBigDecimal = new BigDecimal(amount);
        return amountBigDecimal.multiply(new BigDecimal("100"))
                .setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }


    /**
     * 判断指定对象是否为空
     */
    @SuppressWarnings("unchecked")
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return ((String) value).length() == 0;
        }
        if (value instanceof Collection) {
            return ((Collection<? extends Object>) value).size() == 0;
        }
        if (value instanceof Map) {
            return ((Map<? extends Object, ? extends Object>) value).size() == 0;
        }
        if (value instanceof Date) {
            return false;
        }
        if (value instanceof Boolean) {
            return false;
        }
        if (value instanceof Number) {
            return false;
        }
        if (value instanceof Character) {
            return false;
        }
        if (value instanceof CharSequence) {
            return ((CharSequence) value).length() == 0;
        }

        return false;
    }

    public static String getNonceStr() {
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), CHARSET_UTF_8);
    }

    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

}
