package com.sogou.pay.common.enums;

import java.util.HashMap;

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
    PARTIAL(7),         // 部分支付
    ;

    private static HashMap<String, OrderStatus> map;

    static {
        map = new HashMap<>();
        map.put("NOTPAY", NOTPAY);
        map.put("USERPAYING", USERPAYING);
        map.put("SUCCESS", SUCCESS);
        map.put("FAILURE", FAILURE);
        map.put("CLOSED", CLOSED);
        map.put("REFUND", REFUND);
        map.put("PARTIAL", PARTIAL);
    }

    private OrderStatus(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

    private void setValue(int value) {
        this.value = value;
    }

    public static OrderStatus get(String name) {
        return map.get(name);
    }
}
