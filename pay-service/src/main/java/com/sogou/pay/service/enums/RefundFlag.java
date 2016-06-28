package com.sogou.pay.service.enums;


public enum RefundFlag {
    /**
     * 未退款
     */
    INIT(1),
    /**
     * 部份退款
     */
    PART_REFUND(2),
    /**
     * 退款完成
     */
    SUCCESS(3);

    private final int value;


    private RefundFlag(int value)

    {
        this.value = value;
    }


    public int getValue() {
        return value;
    }
}
