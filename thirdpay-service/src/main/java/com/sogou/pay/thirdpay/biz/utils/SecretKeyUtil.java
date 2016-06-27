package com.sogou.pay.thirdpay.biz.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.sogou.pay.common.utils.DigestUtil;
import com.sogou.pay.common.types.PMap;

public class SecretKeyUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecretKeyUtil.class);

  private static final String RSA = "RSA", SHA1WITHRSA = "SHA1WithRSA", CHARSET = "UTF-8";

  public static String buildSignSource(Map<String, ?> map) {
    if (MapUtils.isEmpty(map)) return "";
    StringBuilder sb = new StringBuilder();
    map.keySet().stream().sorted().forEach(k -> sb.append(k).append('=').append(map.get(k)).append('&'));
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

  public static String aliRSASign(PMap<String, ?> map, String privateKey) {
    return aliRSASign(buildSignSource(filter(map, aliExcludedItems)), privateKey);
  }

  public static String aliRSASign(String content, String privateKey) {
    return signSHA1WithRSA(content, privateKey);
  }

  public static boolean aliRSACheckSign(PMap<String, ?> contextMap, String sign, String publicKey) {
    return aliRSACheckSign(buildSignSource(filter(contextMap, aliExcludedItems)), sign, publicKey);
  }

  public static boolean aliRSACheckSign(String content, String sign, String publicKey) {
    return checkRSASign(content, sign, publicKey);
  }

  public static String aliMD5Sign(PMap<String, ?> contextMap, String key) {
    return signMD5(buildSignSource(filter(contextMap, aliExcludedItems)), key);
  }

  public static boolean aliMD5CheckSign(PMap<String, ?> map, String key, String sign) {
    return checkMD5Sign(sign, buildSignSource(filter(map, aliExcludedItems)), key);
  }

  public static String tenMD5Sign(PMap<String, ?> map, String key) {
    return tenMD5Sign(buildSignSource(filter(map, tenExcludedItems)), key);
  }

  public static String tenMD5Sign(String text, String key) {
    return signMD5(text, getTenpayKey(key), true);
  }

  public static boolean tenMD5CheckSign(PMap<String, ?> map, String key, String sign) {
    return checkMD5Sign(sign, buildSignSource(filter(map, tenExcludedItems)), getTenpayKey(key), true);
  }

  private static String getTenpayKey(String key) {
    return String.format("&key=%s", key);
  }

  public static String unionRSASign(PMap<String, ?> contextMap, String privateKey) {
    return unionRSASign(buildSignSource(filter(contextMap, unionExcludedItems)), privateKey);
  }

  public static String unionRSASign(String content, String privateKey) {
    return signSHA1WithRSA(DigestUtil.SHAEncode(content, CHARSET), privateKey);
  }

  public static boolean unionRSACheckSign(PMap<String, ?> map, String sign, String key) {
    return unionRSACheckSign(buildSignSource(filter(map, unionExcludedItems)), sign, key);
  }

  public static boolean unionRSACheckSign(String content, String sign, String key) {
    return checkRSASign(DigestUtil.SHAEncode(content, CHARSET), sign, key);
  }

  private static String signSHA1WithRSA(String text, String privateKey) {
    return signSHA1WithRSA(text, privateKey, CHARSET);
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

  private static boolean checkRSASign(String content, String sign, String publicKey) {
    return checkRSASign(content, sign, publicKey, CHARSET);
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

  private static String signMD5(String text, String key) {
    return signMD5(text, key, CHARSET, true, false);
  }

  private static String signMD5(String text, String key, boolean isUpperCase) {
    return signMD5(text, key, CHARSET, true, isUpperCase);
  }

  private static String signMD5(String text, String key, String charset, boolean isAppend, boolean isUpperCase) {
    String context = isAppend ? String.format("%s%s", text, key) : String.format("%s%s", key, text);
    String sign = DigestUtil.MD5Encode(context, charset);
    return sign == null || !isUpperCase ? sign : sign.toUpperCase();
  }

  private static boolean checkMD5Sign(String sign, String text, String key) {
    return Objects.equals(sign, signMD5(text, key));
  }

  private static boolean checkMD5Sign(String sign, String text, String key, boolean isUpperCase) {
    return Objects.equals(sign, signMD5(text, key, isUpperCase));
  }

  private static Map<String, ?> filter(Map<String, ?> map, List<String> excludedItems) {
    return map.entrySet().stream()
        .filter(e -> StringUtils.isNotEmpty(getString(e.getValue())) && !isOmitted(e.getKey(), excludedItems))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  private static boolean isOmitted(String item, List<String> excludedItems) {
    for (String s : excludedItems)
      if (s.equalsIgnoreCase(item)) return true;
    return false;
  }

  private static List<String> aliExcludedItems = ImmutableList.of("sign", "sign_type"),
      tenExcludedItems = ImmutableList.of("sign"), unionExcludedItems = ImmutableList.of("signature");

  private static String getString(Object o) {
    return o == null ? null : o.toString();
  }

}
