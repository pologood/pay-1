package com.sogou.pay.common.utils;

import com.google.common.collect.Maps;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by hujunfei Date: 15-1-4 Time: 下午2:22
 */
public class SigUtil {

    public static final String DEFAULT_SIG_NAME = "sign";
    private static final String ALGORITHM = "RSA";
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static Logger logger = LoggerFactory.getLogger(SigUtil.class);

    //使用ThreadLocal变量，防止线程冲突
    private static ThreadLocal<MessageDigest> mdThreadLocal = new ThreadLocal<>();

    // TODO: 增加其他加密方法
    public static MessageDigest getMD() {
        MessageDigest md = mdThreadLocal.get();
        if (md == null) {
            try {
                md = MessageDigest.getInstance("MD5");
                mdThreadLocal.set(md);
            } catch (Exception e) {
                logger.error("Get MessageDigest Error!", e);
            }
        }
        return md;
    }

    /**
     * 校验签名是否正确
     *
     * @param params  签名参数
     * @param secret  密钥
     * @return 签名是否正确
     */
    public static boolean checkSignMD5(String sign, Map params, String secret, String charset) {
        if (params == null || params.size() == 0) {
            throw new IllegalArgumentException("params is null or empty");
        }
        if (StringUtil.isBlank(sign)) {
            throw new IllegalArgumentException("sign is blank");
        }
        if (StringUtil.isBlank(secret)) {
            throw new IllegalArgumentException("secret is blank");
        }

        String calculateSig = signMD5(params, secret, charset);
        // System.out.println("Compute: "+calculateSig+", Origin:"+sig);

        if (sign.trim().equals(calculateSig)) {
            return true;
        }
        logger.warn("Check Sig error Params: " + params + ",Compute: " + calculateSig + ",Origin:" + sign);
        return false;
    }

    /**
     * 计算签名，sigName排除在外，null键和null值也排除在外。不修改原始params
     *
     * @param params  签名参数
     * @param secret  密钥
     * @return 签名结果
     */
    public static String signMD5(Map params, String secret, String charset) {
        if (StringUtil.isBlank(secret)) {
            throw new IllegalArgumentException("secret key is null or empty");
        }
        return encodeMD5(packParams(params, secret), charset);
    }

    @Deprecated
    public static String calculateSig(String sigName, Map params, String secret, String charset) {
        if (StringUtil.isBlank(secret)) {
            throw new IllegalArgumentException("secret key is null or empty");
        }
        return encodeMD5(packParams(params, secret), charset);
    }

    private static String packParams(Map paramMap, String secret) {
        if (paramMap == null) {
            throw new IllegalArgumentException("params is null or empty");
        }
        Map newMap = MapUtil.dropNulls(Maps.newHashMap(paramMap));
        if (MapUtil.isEmpty(newMap)) {
            throw new IllegalArgumentException("params is null or empty");
        }
        List<String> keyList = new ArrayList<String>(paramMap.keySet());
        Collections.sort(keyList);

        //拼接k1=v1k2=v2
        StringBuilder paramStrBuilder = new StringBuilder();
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            Object value = paramMap.get(key);

            if (!StringUtils.isEmpty((String)value)) {
                paramStrBuilder.append(key).append("=").append(value.toString());
                if (i != keyList.size() - 1) {//拼接时，不包括最后一个&字符
                    paramStrBuilder.append("&");
                }
            }
        }

        //拼接secretKey
        paramStrBuilder.append(secret);
        return paramStrBuilder.toString();
    }

    public static void main(String[] args) {
        System.out.println(encodeMD5("accessPlatform=3&appId=5000&bankId=ALIPAY&bgUrl=http://10.134.76.150:8899??op=mszy_pay_feedback&orderAmount=1.00&orderId=20151103113505850805&orderTime=20151103113505&pageUrl=http://www.sogou.com&productDesc=中医支付测试&productName=中医测试&productNum=1&signType=0&version=V1.0idf059023sdkgfo03482fd", "utf-8"));
    }
    private static String encodeMD5(String encodeParam, String charset) {
        if (encodeParam == null) {
            return null;
        }
        try {
            byte b[];
            MessageDigest md = getMD();
            md.reset();
            md.update(encodeParam.getBytes(charset));
            b = md.digest();

            String sig = new String(Hex.encodeHex(b));
            if (logger.isDebugEnabled()) {
                logger.debug("calculateSig: sig=" + sig + "  , map=" + encodeParam);
            }
            return sig;
        } catch (Exception e) {
            logger.error("Encode MD5 Error", e);
            return null;
        }
    }

    public static enum SigType {
        MD5,

    }

    /*public static String aliSdkSign(String content, String privateKey) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }*/

    /*public static boolean rsaverify(String content, String sign,
                                    String ali_public_key, String input_charset) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] encodedKey = Base64.decode(ali_public_key);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
        signature.initVerify(pubKey);
        signature.update(content.getBytes(input_charset));

        boolean bverify = signature.verify(Base64.decode(sign));
        return bverify;
    }*/

    /*public static Result md5sign(PMap<String, String> contextMap,
                                 String md5securityKey, String charset) {
        ResultMap result = ResultMap.build();
        // 去掉map中的空值
        PMap<String, String> signMap = new PMap<String, String>();
        String value = "";
        for (String Key : contextMap.keySet()) {
            value = contextMap.get(Key);
            if (value == null || value.equals("")
                    || Key.equalsIgnoreCase("sign")
                    || Key.equalsIgnoreCase("key")) {
                continue;
            }
            signMap.put(Key, value);
        }
        // 组装签名报文
        String sb = buildSignSource(signMap);
        sb = sb + "&key=" + md5securityKey;
        String signString;
        try {
            signString = MD5Util.MD5Encode(sb, charset).toUpperCase();
        } catch (Exception e) {
            result.withError(ResultStatus.THIRD_SIGN_ERROR);
            return result;
        }
        result.addItem("ChkValue", signString);
        return result;
    }*/

    /*private static String buildSignSource(Map<String, String> contextMap) {
        List keys = new ArrayList<String>(contextMap.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i).toString();
            String value = contextMap.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }*/
}
