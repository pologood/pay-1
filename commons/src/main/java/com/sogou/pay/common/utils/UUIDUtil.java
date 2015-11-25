package com.sogou.pay.common.utils;

import java.util.UUID;

/**
 * Created by hujunfei Date: 15-1-8 Time: 下午7:09
 */
public final class UUIDUtil {

    /**
     * 产生UUID
     *
     * @return uuid
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 产生UUID，去除其中的"-"符号
     *
     * @return uuid
     */
    public static String generateUUIDNoSplit() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 返回UUID的哈希整型绝对值
     *
     * @return uuid
     */
    public static int generateUUIDNumber() {
        return Math.abs(UUID.randomUUID().hashCode());
    }
}
