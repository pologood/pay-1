package com.sogou.pay.service.enums;

/**
 * Created by qibaichao on 2015/4/16.
 */
public enum PayOrderStatus {

    /**
     * 未支付
     */
    INIT(1),
    /**
     * 部份支付
     */
    PAYMENT_IN_PART(2),
    /**
     * 支付完成
     */
    SUCCESS(3),
    /**
     * 无效
     */
    INVALID(4),
    /**
     * 已关闭
     */
    CLOSE(5);

    private final int value;


    private PayOrderStatus(int value)

    {
        this.value = value;
    }


    public int getValue() {
        return value;
    }
}
