/*
 * Copyright 2012-2014 Wanda.cn All right reserved. This software is the
 * confidential and proprietary information of Wanda.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Wanda.cn.
 */
package com.sogou.pay.service.utils;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * The Class StringHelper.
 */
public class StringHelper {

    /**
     * Gets the zero string.
     * 
     * @param length
     *            the length
     * @return the zero string
     */
    public static String getZeroString(int length) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append("0");
        }
        return buffer.toString();
    }

    /**
     * Gets the random string.
     * 
     * @param randomNumberSize
     *            the random number size
     * @param ipAddress
     *            the ip address
     * @return the random string
     */
    public static String getRandomString(int randomNumberSize,
            String ipAddress) {
        long number = 0;
        number += ipToLong(ipAddress);
        Random randomGen = new Random();
        number += randomGen.nextLong();

        /**
         * 取mod
         */
        String defaultString = getZeroString(randomNumberSize);
        StringBuilder modStringBuilder = new StringBuilder();
        modStringBuilder.append("1").append(defaultString);
        long mod = Long.parseLong(modStringBuilder.toString());

        /**
         * 算随机值
         */
        number = number > 0 ? number % mod : Math.abs(number) % mod;

        /**
         * 格式化返回值 为randomNumberSize位
         */
        DecimalFormat df = new DecimalFormat(defaultString);
        return df.format(number);
    }

    /**
     * Ip to long.
     * 
     * @param ipAddress
     *            the ip address
     * @return the long
     */
    public static long ipToLong(String ipAddress) {
        long result = 0;
        String[] atoms = ipAddress.split("\\.");

        for (int i = atoms.length - 1, j = 0; i >= j; i--) {
            result |= (Long.parseLong(atoms[atoms.length - 1 - i]) << (i * 8));
        }

        return result & 0xFFFFFFFF;
    }
}
