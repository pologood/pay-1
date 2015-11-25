package com.sogou.pay.thirdpay.biz.enums;

/**
 * @Author qibaichao
 * @ClassName ClearType
 * @Date 2015年2月16日
 * @Description:
 */
public enum CheckType {
    ALL(0),
    PAYCASH(1),// 支付
    REFUND(3),//退款
    CHARGE(4);//收费


    private CheckType(int value) {
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
