package com.sogou.pay.thirdpay.biz.enums;

/**
 * 订单退款查询接口返回状态参数列表
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/9 15:07
 */
public enum OrderRefundState {
    UNKNOWN(0),         // UNKNOWN-未知退款状态
    SUCCESS(1),         // SUCCESS—退款成功
    PROCESSING(2),      // PROCESSING—退款中
    FAIL(3),         // FAILURE—退款失败
    // OFFLINE 退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，
    // 资金回流到商户的现金帐号，需要商户人工干预，通过线下或者财付通转账的方式进行退款
    OFFLINE(4),
    ;

    private OrderRefundState(int value) {
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
