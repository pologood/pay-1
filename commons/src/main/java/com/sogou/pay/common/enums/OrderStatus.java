package com.sogou.pay.common.enums;

import java.util.HashMap;

/**
 * 订单查询接口返回订单支付状态参数列表
 *
 * @author xiepeidong
 * @date 2016/2/29
 */
public enum OrderStatus {
    NOTPAY(1),          // NOTPAY—未支付
    USERPAYING(2),      // USERPAYING--支付中
    SUCCESS(3),         // SUCCESS—支付完成
    FAILURE(4),        // FAILURE--支付失败
    CLOSED(5),          // CLOSED—已关闭
    REFUND(6),          //REFUND-转入退款
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
