package com.sogou.pay.common.enums;

/**
 * 订单查询接口返回订单支付状态参数列表
 *
 * @author xiepeidong
 * @date 2016/2/29
 */
public enum OrderStatus implements ValueEnum{
    NOTPAY(1),          // 未支付
    USERPAYING(2),      // 支付中
    SUCCESS(3),         // 支付完成
    FAILURE(4),         // 支付失败
    CLOSED(5),          // 已关闭
    REFUND(6),          // 转入退款
    PARTIAL(7);         // 部分支付

    private OrderStatus(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

}
