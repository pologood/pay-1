package com.sogou.pay.service.enums;

import java.util.HashMap;
import java.util.Map;


public enum AgencyCode {
    UNKNOWN(0),
    ALIPAY(1000),
    TENPAY(2000),
    WECHAT(3000),
    CMBC(4000),
    TEST_ALIPAY(1001),
    TEST_TENPAY(2001),
    TEST_WECHAT(3001),
    ;

    private int value;

    AgencyCode(int value) {
        this.value = value;
    }

    private static Map<String, AgencyCode> mappings = new HashMap();

    static {
        mappings.put(ALIPAY.name().toUpperCase(), ALIPAY);
        mappings.put(TENPAY.name().toUpperCase(), TENPAY);
        mappings.put(WECHAT.name().toUpperCase(), WECHAT);
        mappings.put(CMBC.name().toUpperCase(), CMBC);
        mappings.put(TEST_ALIPAY.name().toUpperCase(), TEST_ALIPAY);
        mappings.put(TEST_TENPAY.name().toUpperCase(), TEST_TENPAY);
        mappings.put(TEST_WECHAT.name().toUpperCase(), TEST_WECHAT);
    }

    public static AgencyCode getValue(String party) {
        if (party == null) {
            return UNKNOWN;
        }
        AgencyCode agencyType = mappings.get(party.toUpperCase());
        return agencyType == null ? UNKNOWN : agencyType;
    }

    public int getValue() {
        return value;
    }

    private void setValue(int value) {
        this.value = value;
    }

}
