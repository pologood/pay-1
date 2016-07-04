package com.sogou.pay.thirdpay.biz.utils;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.common.utils.SignUtil;
import com.sogou.pay.common.types.PMap;

public class SecretKeyUtil {

  public static String aliRSASign(PMap<String, ?> map, String privateKey) {
    return aliRSASign(MapUtil.buildSignSource(MapUtil.filter(map, aliExcludedItems)), privateKey);
  }

  public static String aliRSASign(String content, String privateKey) {
    return SignUtil.signSHA1WithRSA(content, privateKey);
  }

  public static boolean aliRSACheckSign(PMap<String, ?> contextMap, String sign, String publicKey) {
    return aliRSACheckSign(MapUtil.buildSignSource(MapUtil.filter(contextMap, aliExcludedItems)), sign, publicKey);
  }

  public static boolean aliRSACheckSign(String content, String sign, String publicKey) {
    return SignUtil.checkRSASign(content, sign, publicKey);
  }

  public static String aliMD5Sign(PMap<String, ?> contextMap, String key) {
    return SignUtil.signMD5(MapUtil.buildSignSource(MapUtil.filter(contextMap, aliExcludedItems)), key);
  }

  public static boolean aliMD5CheckSign(PMap<String, ?> map, String key, String sign) {
    return SignUtil.checkMD5Sign(sign, MapUtil.buildSignSource(MapUtil.filter(map, aliExcludedItems)), key);
  }

  public static String tenMD5Sign(PMap<String, ?> map, String key) {
    return tenMD5Sign(MapUtil.buildSignSource(MapUtil.filter(map, tenExcludedItems)), key);
  }

  public static String tenMD5Sign(String text, String key) {
    return SignUtil.signMD5(text, getTenpayKey(key), true);
  }

  public static boolean tenMD5CheckSign(PMap<String, ?> map, String key, String sign) {
    return SignUtil.checkMD5Sign(sign, MapUtil.buildSignSource(MapUtil.filter(map, tenExcludedItems)),
        getTenpayKey(key), true);
  }

  private static String getTenpayKey(String key) {
    return String.format("&key=%s", key);
  }

  public static String unionRSASign(PMap<String, ?> contextMap, String privateKey) {
    return unionRSASign(MapUtil.buildSignSource(MapUtil.filter(contextMap, unionExcludedItems)), privateKey);
  }

  public static String unionRSASign(String content, String privateKey) {
    return SignUtil.signSHA1WithRSA(SignUtil.shaHex(content), privateKey);
  }

  public static boolean unionRSACheckSign(PMap<String, ?> map, String sign, String key) {
    return unionRSACheckSign(MapUtil.buildSignSource(MapUtil.filter(map, unionExcludedItems)), sign, key);
  }

  public static boolean unionRSACheckSign(String content, String sign, String key) {
    return SignUtil.checkRSASign(SignUtil.shaHex(content), sign, key);
  }

  private static Set<String> aliExcludedItems = ImmutableSet.of("sign", "sign_type"),
      tenExcludedItems = ImmutableSet.of("sign"), unionExcludedItems = ImmutableSet.of("signature");

}
