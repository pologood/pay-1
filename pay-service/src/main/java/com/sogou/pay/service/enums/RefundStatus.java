package com.sogou.pay.service.enums;

/**
 * 退款单退款状态枚举
 */
public enum RefundStatus {
    //平账退款初始状态
    FAIR(0),

    //初始状态
    INIT(1),

    //退款成功
    SUCCESS(2),

    //退款失败
    FAIL(3);

    private final int value;


    private RefundStatus(int value)

    {
        this.value = value;
    }


    public int getValue() {
        return value;
    }
}
