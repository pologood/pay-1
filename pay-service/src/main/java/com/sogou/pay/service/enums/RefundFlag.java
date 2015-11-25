package com.sogou.pay.service.enums;

/**
 * 支付单退款标识枚举
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/22
 */
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
