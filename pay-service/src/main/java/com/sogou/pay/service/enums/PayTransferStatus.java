package com.sogou.pay.service.enums;

/**
 * Created by qibaichao on 2015/6/2.
 * 划款单状态
 */
public enum PayTransferStatus {

    /**
     * 未处理
     */
    INIT(1),
    /**
     * 处理中
     */
    IN_PROCESSING(2),
    /**
     * 支付完成
     */
    SUCCESS(3),
    /**
     * 失败
     */
    FAIL(4),
    /**
     * 退票
     */
    REFUND(5);


    private final int value;


    private PayTransferStatus(int value)

    {
        this.value = value;
    }


    public int getValue() {
        return value;
    }
}
