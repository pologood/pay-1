package com.sogou.pay.service.enums;

/**
 * 退款单退款状态枚举
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/22
 */
public enum RelationStatus {
    /**
     * 未支付
     */
    INIT(0),
    /**
     * 支付成功
     */
    SUCCESS(1),
    /**
     * 支付失败
     */
    FAIL(2),
    /**
     * 平账退款
     */
    REFUND(3);

    private final int value;


    private RelationStatus(int value)

    {
        this.value = value;
    }


    public int getValue() {
        return value;
    }
}
