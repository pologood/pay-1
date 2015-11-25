/**
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/1/9 11:24
 */
package com.sogou.pay.thirdpay.biz.utils;

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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 支付接口专用工具类
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/1/9 11:25
 */
public class Utils {

    /**
     * MD5消息摘要算法
     */
    public static final String ALGORITHM_MD5 = "MD5";
    /**
     * SHA消息摘要算法
     */
    public static final String ALGORITHM_SHA = "SHA";
    /**
     * SHA1withRSA签名算法
     */
    public static final String ALGORITHM_SHA1WITHRSA = "SHA1withRSA";

    /**
     * GBK字符集
     */
    public static final String CHARSET_GBK = "GBK";
    /**
     * UTF-8字符集
     */
    public static final String CHARSET_UTF_8 = "UTF-8";

    /**
     * BASE64编码
     */
    public static final String CODE_BASE64 = "BASE64";
    /**
     * HEX编码
     */
    public static final String CODE_HEX = "HEX";
    /**
     * GBK字符集内容URL编码
     */
    public static final String CODE_URL_GBK = "URL_GBK";
    /**
     * UTF-8字符集内容URL编码
     */
    public static final String CODE_URL_UTF_8 = "URL_UTF-8";

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
     * 时间格式化,依据pattern进行转换.
     */
    public static String dateToString(Date date, String pattern) {
        if ((date == null) || (pattern == null)) {
            return "";
        }
        return new SimpleDateFormat(pattern).format(date).toString();
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
     * 字符串到时间类型转换,依据pattern进行转换.
     */
    public static Date parseDate(String date, String pattern) {
        if ((date == null) || (pattern == null)) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (Exception e) {
            return null;
        }
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

    public static boolean isEmpty(String... strs) {
        for (String str : strs)
            if (str == null || str.length() == 0)
                return true;
        return false;
    }


    /**
     * Populate the JavaBeans properties of the specified bean, based on the specified name/value
     * pairs.
     */
    public static void populate(Object bean, Map<String, ?> properties)
            throws Exception {
        if (properties != null) {
            Map<String, Object> map = new HashMap<String, Object>(properties);
            for (String key : properties.keySet()) {
                if (map.get(key) == null) {
                    map.remove(key);
                }
            }
            try {
                BeanUtils.populate(bean, map);
            } catch (Exception e) {
                throw new Exception("Conversion Exception!");
            }
        }
    }

    /**
     * Copy property values from the origin bean to the destination bean for all cases where the
     * property names are the same.
     */
    public static void copyProperties(Object dest, Object orig)
            throws Exception {
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            throw new Exception("Conversion Exception!");
        }
    }

    /**
     * 对字符串进行消息摘要处理,并把结果转换为指定的编码
     */
    public static String messageDigest(String algorithm, String data,
                                       String charset, String code) throws Exception {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(getBytes(data, charset));
            byte[] bytes = messageDigest.digest();
            return Utils.encode(bytes, code);
        } catch (Exception e) {
            throw new Exception("Message Digest Exception!", e);
        }
    }

    /**
     * 用指定的编码对指定的值编码
     */
    public static String encode(byte[] data, String code) throws Exception {
        if (code == null) {
            return new String(data);
        }
        if (Utils.CODE_BASE64.equals(code)) {
            return Base64.encodeBase64String(data);
        } else if (Utils.CODE_HEX.equals(code)) {
            return Hex.encodeHexString(data);
        } else if (Utils.CODE_URL_UTF_8.equals(code)) {
            return URLEncoder.encode(new String(data, CHARSET_UTF_8), CHARSET_UTF_8);
        } else if (Utils.CODE_URL_GBK.equals(code)) {
            return URLEncoder.encode(new String(data, CHARSET_GBK), CHARSET_GBK);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 用指定的编码对指定的值反编码
     */
    public static String decode(String data, String code) throws Exception {
        if (code == null) {
            return new String(data);
        }
        if (Utils.CODE_BASE64.equals(code)) {
            return new String(Base64.decodeBase64(data));
        } else if (Utils.CODE_HEX.equals(code)) {
            return new String(Hex.decodeHex(data.toCharArray()));
        } else if (Utils.CODE_URL_UTF_8.equals(code)) {
            return URLDecoder.decode(data, CHARSET_UTF_8);
        } else if (Utils.CODE_URL_GBK.equals(code)) {
            return URLDecoder.decode(data, CHARSET_GBK);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 获取指定字符串和字符编码的字节数组
     *
     * @param data    字符串
     * @param charset 字符集默认UTF-8
     */
    public static byte[] getBytes(String data, String charset) {
        try {
            return data.getBytes(charset == null ? CHARSET_UTF_8 : charset);
        } catch (UnsupportedEncodingException e) {
            return data.getBytes();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 转换指定字符串为指定字符编码
     *
     * @param str     字符串
     * @param charset 字符集默认UTF-8
     */
    public static String newString(String str, String charset) {
        charset = charset == null ? CHARSET_UTF_8 : charset;
        try {
            return new String(str.getBytes(charset), charset);
        } catch (Exception e) {
            return str;
        }
    }

    public static String getNonceStr() {
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), CHARSET_UTF_8);
    }

    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

}
