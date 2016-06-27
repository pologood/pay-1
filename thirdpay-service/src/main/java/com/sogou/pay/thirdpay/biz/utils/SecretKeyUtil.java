package com.sogou.pay.thirdpay.biz.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sogou.pay.common.utils.DigestUtil;
import com.google.common.collect.ImmutableList;
import com.sogou.pay.common.types.PMap;

public class SecretKeyUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecretKeyUtil.class);

  private static final String RSA = "RSA", SHA1WITHRSA = "SHA1WithRSA", CHARSET = "UTF-8";

  public static String buildSignSource(Map<String, String> contextMap) {
    if (MapUtils.isEmpty(contextMap)) return "";
    StringBuilder sb = new StringBuilder();
    contextMap.keySet().stream().sorted().forEach(k -> sb.append(k).append('=').append(contextMap.get(k)).append('&'));
    return sb.substring(0, sb.length() - 1);
  }

  public static String loadKeyFromFile(String keyFilePath) {
    try {
      return StringUtils.join(Files.readAllLines(Paths.get(keyFilePath)), null);
    } catch (Exception e) {
      LOGGER.error(String.format("read file error:path=%s", keyFilePath), e);
      return null;
    }
  }

  public static String aliRSASign(PMap<String, String> contextMap, String privateKey, String charset) {
    return aliRSASign(buildSignSource(contextMap), privateKey, charset);
  }

  public static String aliRSASign(String content, String privateKey, String charset) {
    return signSHA1WithRSA(content, privateKey, charset);
  }

  public static boolean aliRSACheckSign(PMap<String, String> contextMap, String sign, String publicKey) {
    return aliRSACheckSign(buildSignSource(filter(contextMap, aliExcludedItems)), sign, publicKey);
  }

  public static boolean aliRSACheckSign(String content, String sign, String publicKey) {
    return checkRSASign(content, sign, publicKey, CHARSET);
  }

  public static String aliMD5Sign(PMap<String, String> contextMap, String key, String charset) {
    return signMD5(buildSignSource(filter(contextMap, aliExcludedItems)), key, charset, true, false);
  }

  public static boolean aliMD5CheckSign(PMap<String, String> map, String key, String sign, String charset) {
    return checkMD5Sign(sign, buildSignSource(filter(map, aliExcludedItems)), key, charset, true, false);
  }

  public static String tenMD5Sign(PMap<String, String> map, String key, String charset) {
    return tenMD5Sign(buildSignSource(filter(map, tenExcludedItems)), key, charset);
  }

  public static String tenMD5Sign(String text, String key, String charset) {
    return signMD5(text, String.format("&key=%s", key), charset, true, true);
  }

  public static boolean tenMD5CheckSign(PMap<String, String> map, String key, String sign, String charset) {
    return checkMD5Sign(sign, buildSignSource(filter(map, tenExcludedItems)), String.format("&key=%s", key), charset,
        true, true);
  }

  public static String unionRSASign(PMap<String, String> contextMap, String privateKey, String charset) {
    return unionRSASign(buildSignSource(filter(contextMap, unionExcludedItems)), privateKey, charset);
  }

  public static String unionRSASign(String content, String privateKey, String charset) {
    return signSHA1WithRSA(DigestUtil.SHAEncode(content, charset), privateKey, charset);
  }

  public static boolean unionRSACheckSign(PMap<String, String> map, String sign, String key, String charset) {
    return unionRSACheckSign(buildSignSource(filter(map, unionExcludedItems)), sign, key, charset);
  }

  public static boolean unionRSACheckSign(String content, String sign, String key, String charset) {
    return checkRSASign(DigestUtil.SHAEncode(content, charset), sign, key, charset);
  }

  private static String signSHA1WithRSA(String text, String privateKey, String charset) {
    try {
      Signature signature = Signature.getInstance(SHA1WITHRSA);

      signature.initSign(
          KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey))));
      signature.update(text.getBytes(charset));

      return Base64.getEncoder().encodeToString(signature.sign());
    } catch (Exception e) {
      LOGGER.error("signSHA1WithRSA error", e);
      return null;
    }
  }

  private static boolean checkRSASign(String content, String sign, String publicKey, String charset) {
    try {
      Signature signature = Signature.getInstance(SHA1WITHRSA);

      signature.initVerify(
          KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey))));
      signature.update(content.getBytes(charset));

      return signature.verify(Base64.getDecoder().decode(sign));
    } catch (Exception e) {
      LOGGER.error("checkRSASign error", e);
      return false;
    }
  }

  private static String signMD5(String text, String key, String charset, boolean isAppend, boolean isUpperCase) {
    String context = isAppend ? String.format("%s%s", text, key) : String.format("%s%s", key, text);
    String sign = DigestUtil.MD5Encode(context, charset);
    return isUpperCase ? sign.toUpperCase() : sign;
  }

  private static boolean checkMD5Sign(String sign, String text, String key, String charset, boolean isAppend,
      boolean isUpperCase) {
    return Objects.equals(sign, signMD5(text, key, charset, isAppend, isUpperCase));
  }

  private static Map<String, String> filter(Map<String, String> map, List<String> excludedItems) {
    return map.entrySet().stream()
        .filter(e -> StringUtils.isNotEmpty(e.getValue()) && !isOmitted(e.getKey(), excludedItems))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  private static boolean isOmitted(String item, List<String> excludedItems) {
    for (String s : excludedItems)
      if (s.equalsIgnoreCase(item)) return true;
    return false;
  }

  private static List<String> aliExcludedItems = ImmutableList.of("sign", "sign_type", "key"),
      tenExcludedItems = ImmutableList.of("sign", "key"), unionExcludedItems = ImmutableList.of();

}
