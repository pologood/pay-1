package com.sogou.pay.thirdpay.biz.enums;

/**
 * @Author qibaichao
 * @ClassName ClearType
 * @Date 2015年2月16日
 * @Description:
 */
public enum CheckType {
    ALL(0),
    PAID(1),// 得到支付
    REFUND(3),// 主动退款
    CHARGED(4),// 被收费(手续费)
    WITHDRAW(5);// 主动提现


    private CheckType(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

}
