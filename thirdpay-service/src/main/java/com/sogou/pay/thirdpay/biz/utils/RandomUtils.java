package com.sogou.pay.thirdpay.biz.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @Author qibaichao
 * @ClassName RandomUtils
 * @Date 2015年2月16日
 * @Description:随机参数
 */
public class RandomUtils {

    private static Random random = new Random(1000);

    /**
     * 随机三位数
     * Next value.
     *
     * @return the string
     */
    public static String nextThreeValue() {
        int value = random.nextInt();
        if (value < 100) {
            value += 100;
        }
        return String.valueOf(value);
    }

    /**
     * 32位随机字符
     * @return
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "").toUpperCase();
        return uuid;
    }

}
