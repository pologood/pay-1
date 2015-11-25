package com.sogou.pay.service.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qibaichao on 2015/6/5.
 */
public enum TenpayPayTranferTradeState {

    INIT(1, "初始状态"),

    PENDING_AUDIT(2, "待审核"),

    CAN_PAY(3, "可付款"),

    PAY_ERROR(4, "付款失败"),

    IN_PROCESSING(5, "处理中"),

    COMPLETE(6, "受理完成"),

    CANCELLED(7, "已取消");

    private final int value;

    private final String desc;

    private static Map<Integer, TenpayPayTranferTradeState> mappings = new HashMap();

    static {
        mappings.put(INIT.getValue(), INIT);
        mappings.put(PENDING_AUDIT.getValue(), PENDING_AUDIT);
        mappings.put(CAN_PAY.getValue(), CAN_PAY);
        mappings.put(PAY_ERROR.getValue(), PAY_ERROR);
        mappings.put(IN_PROCESSING.getValue(), IN_PROCESSING);
        mappings.put(COMPLETE.getValue(), COMPLETE);
        mappings.put(CANCELLED.getValue(), CANCELLED);
    }

    private TenpayPayTranferTradeState(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static Map<Integer, TenpayPayTranferTradeState> getMappings() {
        return mappings;
    }

    public static void setMappings(Map<Integer, TenpayPayTranferTradeState> mappings) {
        TenpayPayTranferTradeState.mappings = mappings;
    }

    public static void main(String args[]){
        System.out.println(mappings.get(1).getDesc());
    }
}
