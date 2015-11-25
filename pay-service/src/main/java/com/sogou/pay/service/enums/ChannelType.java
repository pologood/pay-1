package com.sogou.pay.service.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hujunfei Date: 15-1-4 Time: 下午5:04
 * 调用接口类型：即时到账、网关支付、信用卡支付、退款、提现
 */
public enum ChannelType {
    UNKNOWN(0),
    BANK(1),        // 网银
    QUICK(2),       // 快捷支付
    ACCOUNT(3),     // 账户支付
    ONEKEY(4),      // 一键支付
    QRCODE(5),      // 扫码支付
//    REFUND(10),
//    WITHDRAW(21)
    ;

    private static Map<String, ChannelType> mappings = new HashMap();
    private static final ChannelType DEFAULT_TYPE = ACCOUNT;

    static {
        mappings.put("account".toUpperCase(), ACCOUNT);
        mappings.put("bank".toUpperCase(), BANK);
        mappings.put("quick".toUpperCase(), QUICK);
        mappings.put("onekey".toUpperCase(), ONEKEY);
        mappings.put("qrcode".toUpperCase(), QRCODE);
    }


    private ChannelType(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

    private void setValue(int value) {
        this.value = value;
    }

    public static ChannelType defaultType() {
        return DEFAULT_TYPE;
    }

    /**
     * @param str
     * @return 返回相应的ChannelType，查找不到则返回UNKNOWN
     */
    public static ChannelType getType(String str) {
        if (str == null || str.length() == 0) {
            return UNKNOWN;
        }
        ChannelType terminalType = mappings.get(str.toUpperCase());
        return terminalType == null ? UNKNOWN : terminalType;
    }

    /**
     * @param channel
     * @param defaultType
     * @return 返回相应的ChannelType，查找不到则返回defaultType
     */
    public static ChannelType getType(String channel, ChannelType defaultType) {
        ChannelType type = getType(channel);
        return type == UNKNOWN ? defaultType : type;
    }
}
