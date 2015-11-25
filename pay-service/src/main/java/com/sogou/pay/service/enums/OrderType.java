package com.sogou.pay.service.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujunfei Date: 15-1-4 Time: 下午5:07
 * 订单类型：支付、充值、退款、提现等
 */
public enum OrderType {
    PAYCASH(1),         // 支付
    RECHARGE(2),    // 充值
    REFUND(3),      // 退款
    //    WITHDRAW(4),    // 提现
    //    PAY_BALANCE(2),     // 余额支付
    ;

    private static List<OrderType> orderList =new ArrayList<>();
    static {


        orderList.add(PAYCASH);
        orderList.add(RECHARGE);
        orderList.add(REFUND);
    }


    public static List<OrderType> getOrderList() {
        return orderList;
    }

    public static void setOrderList(List<OrderType> orderList) {
        OrderType.orderList = orderList;
    }

    private OrderType(int value) {
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
