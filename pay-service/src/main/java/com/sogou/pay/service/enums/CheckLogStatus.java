package com.sogou.pay.service.enums;

import java.util.HashSet;


public enum CheckLogStatus {

    /**
     * 初始状态
     */
    INIT(0),
    /**
     * 下载成功
     */
    DOWNLOADSUCCESS(1),
    /**
     * 对账成功
     */
    SUCCESS(2),
    /**
     * 失败
     */
    FAIL(3);

    /**
     * The value.
     */
    private final int value;

    private CheckLogStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    private static HashSet<Integer> hashSet;

    static {
        hashSet = new HashSet<Integer>();
        hashSet.clear();
        for (CheckLogStatus returnStatus : CheckLogStatus.values()) {
            hashSet.add(returnStatus.value());
        }
    }

    public static boolean isDefined(int value) {
        if (hashSet.contains(value)) {
            return true;
        }
        return false;
    }
}
