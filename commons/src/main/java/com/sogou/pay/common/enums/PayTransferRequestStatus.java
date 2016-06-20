package com.sogou.pay.common.enums;


public enum PayTransferRequestStatus {

    /**
     * 未提交
     */
    INIT(0),

    /**
     * 提交中
     */
    REQUEST_PROCESSING(1),
    /**
     * 提交成功
     */
    REQUEST_SUCCESS(2),
    /**
     * 提交失败
     */
    REQUEST_FAIL(3);


    private final int value;


    private PayTransferRequestStatus(int value)

    {
        this.value = value;
    }


    public int getValue() {
        return value;
    }
}
