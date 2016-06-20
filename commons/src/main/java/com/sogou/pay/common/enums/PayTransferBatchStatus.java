package com.sogou.pay.common.enums;


public enum PayTransferBatchStatus {

    /**
     * 初始状态
     */
    INIT(1),
    /**
     * 审核通过
     */
    AUDIT_PASS(2),
    /**
     * 审核不通过
     */
    AUDIT_NOT_PASS(3),
    /**
     * 银行处理中
     */
    IN_PROCESSING(4),
    /**
     * 受理完成
     */
    COMPLETION(5),
    /**
     * 付款失败
     */
    FAIL(6),
    
    FINAL_APPROVED(14);
    private final int value;

    private PayTransferBatchStatus(int value)

    {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
