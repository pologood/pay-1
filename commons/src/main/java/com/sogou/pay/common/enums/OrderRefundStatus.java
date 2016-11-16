package com.sogou.pay.common.enums;

/**
 * 订单退款查询接口返回状态参数列表
 *
 * @author xiepeidong
 * @date 2016/2/29
 */
public enum OrderRefundStatus implements ValueEnum{
    UNKNOWN(0),         // UNKNOWN-未知退款状态
    SUCCESS(1),         // SUCCESS—退款成功
    PROCESSING(2),      // PROCESSING—退款中
    FAIL(3),            // FAILURE—退款失败
    OFFLINE(4);         //OFFLINE 退款到银行发现用户的卡作废或者冻结了,导致原路退款银行卡失败,资金回流到商户的现金帐号,需要商户人工干预,通过线下或者财付通转账的方式进行退款

    private OrderRefundStatus(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

}
