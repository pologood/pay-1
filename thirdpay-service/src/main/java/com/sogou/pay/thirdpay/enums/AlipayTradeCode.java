package com.sogou.pay.thirdpay.enums;

/**
 * Created by qibaichao on 2015/3/5.
 * 支付宝交易类型代码
 */
public enum AlipayTradeCode {

    /**交易类型代码*/
    TRADE_CODE_TRANSFER("3011"),// 转账（含红包、集分宝等）
    TRADE_CODE_CHARGE("3012"),// 收费
    TRADE_CODE_RECHARGE("4003"),//充值
    TRADE_CODE_CASH("5004"),//提现
    TRADE_CODE_REFUND("5103"),//退票
    TRADE_CODE_PAY("6001"),//在线支付

    /**子业务类型代码*/
    TRADE_CODE_SUB_REFUND("301101");


    private AlipayTradeCode(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
