package com.sogou.pay.notify.enums;

import java.util.HashSet;

/**
 * @Author qibaichao
 * @ClassName NotifyCodeEnum
 * @Date 2014年9月18日
 * @Description:
 */
public enum NotifyTypeEnum {

    PAY_NOTIFY(1, "支付通知"),

    REFUND_NOTIFY(2, "退款通知");

    /** The value. */
    private final int value;

    /** The desc. */
    private final String desc;

    private NotifyTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int value() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    private static HashSet<Integer> hashSet;

    static {
        hashSet = new HashSet<Integer>();
        hashSet.clear();
        for (NotifyTypeEnum businessCode : NotifyTypeEnum.values()) {
            hashSet.add(businessCode.value());
        }
    }

    public static boolean isDefined(int value) {
        if (hashSet.contains(value)) {
            return true;
        }
        return false;
    }

}
