package com.sogou.pay.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import java.security.MessageDigest;


public class MD5Util {
    public static String MD5Encode(String origin, String charset) {
        if(charset==null)
            charset = "UTF-8";
        try {
            return DigestUtils.md5Hex(origin.getBytes(charset));
        }catch (Exception ex){
            return null;
        }
    }

    public static String SHAEncode(String origin, String charset) {
        if(charset==null)
            charset = "UTF-8";
        try {
            return DigestUtils.shaHex(origin.getBytes(charset));
        }catch (Exception ex){
            return null;
        }
    }
}
