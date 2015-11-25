package com.sogou.pay.thirdpay.biz.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.Base64;
import com.sogou.pay.common.utils.MD5Util;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.thirdpay.biz.utils.billpay.BillMD5Util;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 12:07
 */
public class SecretKeyUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecretKeyUtil.class);


    private static final String ALGORITHM = "RSA";
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String ALIPAY_PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
    /**
     * 支付宝客户端支付--RSA加密
     */
    public static String aliClientRsaSign(String content, String privateKey, String charset) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(charset));

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 支付宝RSA验签名检查
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 支付宝公钥
     * @return 布尔值
     */
    public static boolean aliClientRsaCheck(String content, String sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            byte[] encodedKey = Base64.decode(publicKey);
            PublicKey pubKey = keyFactory
                    .generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));

            return signature.verify(Base64.decode(sign));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 财付通、微信支付MD5签名 contextMap 签名参数 md5securityKey MD5密钥 charset 参数编码
     */
    public static ResultMap tenMd5sign(PMap<String, String> contextMap,
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
            result.withError(ResultStatus.PAY_SING_ERROR);
            return result;
        }
        result.addItem("signValue", signString);
        return result;
    }

    /**
     * 财付通对账MD5签名 contextMap 签名参数 md5securityKey MD5密钥 charset 参数编码
     */
    public static String tenMd5sign(String parameters, String md5securityKey,
                                    String charset) {
        StringBuffer sb = new StringBuffer();

        sb.append(parameters);

        sb.append("&");

        sb.append("key=" + md5securityKey);

        String sign = MD5Util.MD5Encode(sb.toString(), charset).toLowerCase();

        return sign;
    }

    /**
     * 支付宝支付MD5签名 contextMap 签名参数 md5securityKey MD5密钥 charset 参数编码
     */
    public static ResultMap aliMd5sign(PMap<String, String> contextMap,
                                       String md5securityKey, String charset) {
        ResultMap result = ResultMap.build();
        // 去掉map中的空值
        PMap<String, String> signMap = new PMap<String, String>();
        String value = "";
        for (String Key : contextMap.keySet()) {
            value = contextMap.get(Key);
            if (value == null || value.equals("")
                    || Key.equalsIgnoreCase("sign") || Key.equalsIgnoreCase("sign_type")
                    || Key.equalsIgnoreCase("key")) {
                continue;
            }
            signMap.put(Key, value);
        }
        // 组装签名报文
        String sb = buildSignSource(signMap);
        sb = sb + md5securityKey;
        String signString;
        try {
            signString = md5(sb);
        } catch (Exception e) {
            result.withError(ResultStatus.PAY_SING_ERROR);
            return result;
        }
        result.addItem("signValue", signString);
        return result;
    }

    /**
     * 构建请求报文
     */
    private static String buildSignSource(Map<String, String> contextMap,String...agencyFlag) {
        List keys = new ArrayList<String>(contextMap.keySet());
        if(null == agencyFlag || agencyFlag.length == 0)
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
    }

    /**
     * 财付通、微信 MD5验证签名 contextMap 验证参数 md5securityKey MD5密钥 returnSign 返回参数中的签名 charset 签名编码
     */
    public static boolean tenCheckMd5sign(PMap<String, String> contextMap,
                                          String md5securityKey, String returnSign,
                                          String charset) {
        ResultMap result = ResultMap.build();
        // 去掉map中的空值
        PMap<String, String> signMap = new PMap<String, String>();
        String value = "";
        for (String Key : contextMap.keySet()) {
            value = contextMap.get(Key);
            if (value == null || value.equals("")
                    || Key.equalsIgnoreCase("sign") || Key.equalsIgnoreCase("Sign")
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
            LOGGER.info("compute sign={}, return sign={}", signString, returnSign);
        } catch (Exception e) {
            return false;
        }
        return returnSign.equals(signString);
    }

    /**
     * 支付宝MD5验证签名 contextMap 验证参数 md5securityKey MD5密钥 returnSign 返回参数中的签名 charset 签名编码
     */
    public static boolean aliCheckMd5sign(PMap<String, String> contextMap,
                                          String md5securityKey, String returnSign,
                                          String charset) {
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
        sb = sb + md5securityKey;
        String signString;
        try {
            signString = md5(sb);
        } catch (Exception e) {
            return false;
        }
        return returnSign.equals(signString);
    }

    /**
     * 支付宝RSA验证签名 contextMap  returnSign 返回参数中的签名  publicCertFilePath 支付宝公钥路径
     */
    public static boolean aliCheckRSAsign(PMap<String, String> contextMap, String sign,
                                          String publicCertFilePath) {
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
        String content = buildSignSource(signMap);
        
        return aliClientRsaCheck(content, sign, ALIPAY_PUB_KEY);
    }
    
    /**
     * 支付宝WapMD5验证签名 contextMap 验证参数 md5securityKey MD5密钥 returnSign 返回参数中的签名 charset 签名编码
     */
    public static boolean aliWapCheckMd5sign(PMap<String, String> contextMap,
                                             String md5securityKey, String returnSign,
                                             String charset) {
        // 组装签名报文则只需对以下数据进行验签：
        //service=alipay.wap.trade.create.direct&v=1.0&sec_id=0001&notify_data=<notify>…</notify>
        String sb = "service=" + contextMap.get("service")
                + "&v=" + contextMap.get("v")
                + "&sec_id=" + contextMap.get("sec_id")
                + "&notify_data=" + contextMap.get("notify_data");
        sb = sb + md5securityKey;
        String signString;
        try {
            signString = md5(sb);
        } catch (Exception e) {
            return false;
        }
        return returnSign.equals(signString);
    }

    public static boolean billCheckRSAsign(PMap<String, String> signMap,
                                            String pubKeyPath, String sign){
        String sb = buildSignSource(signMap,"99BILL");
        Pkipair pair = new Pkipair();
        boolean isTrue;
        try {
            isTrue = pair.enCodeByCer(sb, sign,pubKeyPath);
            return isTrue;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 对字符串进行MD5签名
     *
     * @param text 明文
     * @return 密文
     */
    private static String md5(String text) throws Exception {

        return DigestUtils.md5Hex(getContentBytes(text, "utf-8"));

    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws Exception
     * @throws
     * @throws
     */
    private static byte[] getContentBytes(String content, String charset) throws Exception {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (Exception e) {
            throw new Exception("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    /**
     * 快钱获取签名
     */
    public static String billSignMsg(PMap signMsgMap, String path) {

        String signMsg = buildSignSource(signMsgMap);
        String base64 = "";
        try {
            // 密钥仓库
            KeyStore ks = KeyStore.getInstance("PKCS12");
            // 读取密钥仓库
            FileInputStream ksfis = new FileInputStream(path);
            BufferedInputStream ksbufin = new BufferedInputStream(ksfis);

            char[] keyPwd = "123456".toCharArray();
            //char[] keyPwd = "YaoJiaNiLOVE999Year".toCharArray();
            ks.load(ksbufin, keyPwd);
            // 从密钥仓库得到私钥
            PrivateKey priK = (PrivateKey) ks.getKey("test-alias", keyPwd);
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(priK);
            signature.update(signMsg.getBytes("utf-8"));
            sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            base64 = encoder.encode(signature.sign());

        } catch (FileNotFoundException e) {
            System.out.println("文件找不到");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("test = " + base64);
        return base64;
    }

    /**
     * 快钱支付MD5签名 contextMap 签名参数 charset 参数编码
     */
    public static ResultMap billMd5sign(PMap<String, String> contextMap, String charset) {
        ResultMap result = ResultMap.build();
        // 去掉map中的空值
        PMap<String, String> signMap = new PMap<String, String>();
        String value = "";
        for (String Key : contextMap.keySet()) {
            value = contextMap.get(Key);
            if (value == null || value.equals("")) {
                continue;
            }
            signMap.put(Key, value);
        }
        // 组装签名报文
        String sb = buildSignSource(signMap);
        String signMsgVal = "";
        try {
            signMsgVal = BillMD5Util.md5Hex(sb.getBytes(charset)).toUpperCase();
        } catch (Exception e) {
            result.withError(ResultStatus.PAY_SING_ERROR);
            return result;
        }
        result.addItem("signValue", signMsgVal);
        return result;
    }


}
