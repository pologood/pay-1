package com.sogou.pay.thirdpay.biz.enums;

/**
 * Created by qibaichao on 2015/3/5.
 * 支付宝交易类型代码
 */
public enum AlipayTradeCode {

    /**交易类型代码*/
    TRADE_CODE_TRANSFER("3011"),// 转账（含红包、集分宝等），包含我们向他人的退款
    TRADE_CODE_CHARGE("3012"),// 收费，我们被收的费
    TRADE_CODE_RECHARGE("4003"),//充值，我们的充值
    TRADE_CODE_CASH("5004"),//提现，我们的提现
    TRADE_CODE_REFUND("5103"),//退票，来自他人的退款
    TRADE_CODE_PAY("6001"),//在线支付，来自他人的付款

    /**子业务类型代码*/
    TRADE_CODE_SUB_REFUND("301101");


    private AlipayTradeCode(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

}
