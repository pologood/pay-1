package com.sogou.pay.notify.enums;


public enum NotifyStatus {

    /**
     * 通知（pay_notify）:初始
     */
    INIT(0, "INIT"),
    /**
     * 通知（pay_notify）:成功
     */
    SUCCESS(1, "SUCCESS"),
    /**
     * 通知（pay_notify）:失败
     */
    FAIL(2, "FAIL");

    private final int value;

    private final String desc;

    NotifyStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
