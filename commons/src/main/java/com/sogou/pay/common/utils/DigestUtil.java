package com.sogou.pay.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class DigestUtil {

  private static final String DEFAULT_CHARSET = "UTF-8";

  private static String getCharset(String charset) {
    return charset == null || charset.length() == 0 ? DEFAULT_CHARSET : charset;
  }

  public static String MD5Encode(String origin, String charset) {
    try {
      return DigestUtils.md5Hex(origin.getBytes(getCharset(charset)));
    } catch (Exception ex) {
      return null;
    }
  }

  public static String SHAEncode(String origin, String charset) {
    try {
      return DigestUtils.shaHex(origin.getBytes(getCharset(charset)));
    } catch (Exception ex) {
      return null;
    }
  }
}
