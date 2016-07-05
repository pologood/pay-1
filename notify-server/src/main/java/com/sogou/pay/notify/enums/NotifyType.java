package com.sogou.pay.notify.enums;

public enum NotifyType {

    PAY_NOTIFY(1, "支付通知"),
    REFUND_NOTIFY(2, "退款通知"),
    TRANSFER_NOTIFY(3, "付款通知");

    private final int value;

    private final String desc;

    private NotifyType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int value() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
