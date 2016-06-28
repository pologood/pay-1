package com.sogou.pay.service.enums;

import java.util.ArrayList;
import java.util.List;


public enum OrderType {
    PAY(1),         // 支付
    RECHARGE(2),    // 充值
    REFUND(3),      // 退款
    WITHDRAW(5),    // 提现
    //    PAY_BALANCE(2),     // 余额支付
    ;

    private static List<OrderType> orderList =new ArrayList<>();
    static {


        orderList.add(PAY);
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
