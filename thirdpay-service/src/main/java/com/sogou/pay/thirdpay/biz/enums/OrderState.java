package com.sogou.pay.thirdpay.biz.enums;

/**
 * 订单查询接口返回订单支付状态参数列表
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/4 18:23
 */
public enum OrderState {
    NOTPAY(1),          // NOTPAY—未支付
    USERPAYING(2),      // USERPAYING--支付中
    SUCCESS(3),         // SUCCESS—支付完成
    FAILURE(4),        // FAILURE--支付失败
    CLOSED(5),          // CLOSED—已关闭
    REFUND(6),          //REFUND-转入退款
    ;

    private OrderState(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

    private void setValue(int value) {
        this.value = value;
    }
}
