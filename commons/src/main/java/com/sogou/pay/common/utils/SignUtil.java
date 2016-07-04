package com.sogou.pay.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

public class SignUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(SignUtil.class);

  private static final String RSA = "RSA", SHA1WITHRSA = "SHA1WithRSA", CHARSET = "UTF-8";

  public static String md5Hex(String origin, String charset) {
    try {
      return DigestUtils.md5Hex(origin.getBytes(charset));
    } catch (Exception e) {
      LOGGER.error("[md5Hex]error", e);
      return null;
    }
  }

  public static String md5Hex(String origin) {
    return md5Hex(origin, CHARSET);
  }

  public static String shaHex(String origin, String charset) {
    try {
      return DigestUtils.shaHex(origin.getBytes(charset));
    } catch (Exception e) {
      LOGGER.error("[shaHex]error", e);
      return null;
    }
  }

  public static String shaHex(String origin) {
    return shaHex(origin, CHARSET);
  }

  public static String signSHA1WithRSA(String text, String privateKey, String charset) {
    try {
      Signature signature = Signature.getInstance(SHA1WITHRSA);

      signature.initSign(
          KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey))));
      signature.update(text.getBytes(charset));

      return Base64.getEncoder().encodeToString(signature.sign());
    } catch (Exception e) {
      LOGGER.error("[signSHA1WithRSA]error", e);
      return null;
    }
  }

  public static String signSHA1WithRSA(String text, String privateKey) {
    return signSHA1WithRSA(text, privateKey, CHARSET);
  }

  private static boolean checkRSASign(String content, String sign, String publicKey, String charset) {
    try {
      Signature signature = Signature.getInstance(SHA1WITHRSA);

      signature.initVerify(
          KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey))));
      signature.update(content.getBytes(charset));

      return signature.verify(Base64.getDecoder().decode(sign));
    } catch (Exception e) {
      LOGGER.error("[checkRSASign]error", e);
      return false;
    }
  }

  public static boolean checkRSASign(String content, String sign, String publicKey) {
    return checkRSASign(content, sign, publicKey, CHARSET);
  }

  public static String signMD5(String text, String key, String charset, boolean isAppend, boolean isUpperCase) {
    String context = isAppend ? String.format("%s%s", text, key) : String.format("%s%s", key, text);
    String sign = md5Hex(context, charset);
    return isUpperCase ? StringUtils.upperCase(sign) : sign;
  }

  public static String signMD5(String text, String key) {
    return signMD5(text, key, CHARSET, true, false);
  }

  public static String signMD5(String text, String key, boolean isUpperCase) {
    return signMD5(text, key, CHARSET, true, isUpperCase);
  }

  public static boolean checkMD5Sign(String sign, String text, String key) {
    return Objects.equals(sign, signMD5(text, key));
  }

  public static boolean checkMD5Sign(String sign, String text, String key, boolean isUpperCase) {
    return Objects.equals(sign, signMD5(text, key, isUpperCase));
  }

}
