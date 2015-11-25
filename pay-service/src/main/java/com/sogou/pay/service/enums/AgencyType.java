package com.sogou.pay.service.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hujunfei Date: 15-1-7 Time: 下午4:16
 */
public enum AgencyType {
    UNKNOWN(0),
    ALIPAY(1000),
    TENPAY(2000),
    WECHAT(3000),  // 微信
    BILL99(4000),
    ;
    // SGPAY(10)

    private int value;

    AgencyType(int value) {
        this.value = value;
    }

    // 支持的第三方支付机构映射表
    private static Map<String, AgencyType> mappings = new HashMap();

    private static List<String> agencyList =new ArrayList<>();

    static {
        mappings.put(ALIPAY.name().toUpperCase(), ALIPAY);
        mappings.put(TENPAY.name().toUpperCase(), TENPAY);
        mappings.put(WECHAT.name().toUpperCase(), WECHAT);
        mappings.put(BILL99.name().toUpperCase(), BILL99);
        // mappings.put(UNIONPAY.name().toUpperCase(), UNIONPAY);
        // mappings.put(SGPAY.name().toUpperCase(), SGPAY);

        agencyList.add(ALIPAY.name().toUpperCase());
        agencyList.add(TENPAY.name().toUpperCase());
        agencyList.add(WECHAT.name().toUpperCase());
        agencyList.add(BILL99.name().toUpperCase());
    }

    /**
     * 返回对应的AgencyType，空值或不匹配值返回UNKNOWN，无默认值
     *
     * @param party
     * @return
     */
    public static AgencyType getType(String party) {
        if (party == null) {
            return UNKNOWN;
        }
        AgencyType agencyType = mappings.get(party.toUpperCase());
        return agencyType == null ? UNKNOWN : agencyType;
    }

    /**
     * 同getType(String agency)，有默认值，若结果为UNKNOWN，则返回defaultType
     *
     * @param agency
     * @param defaultType UNKNOWN时返回的默认值
     * @return
     * @see com.sogou.pay.service.enums.AgencyType#getType(String)
     */
    public static AgencyType getType(String agency, AgencyType defaultType) {
        AgencyType type = getType(agency);
        return type == UNKNOWN ? defaultType : type;
    }

    public int getValue() {
        return value;
    }

    private void setValue(int value) {
        this.value = value;
    }

    public static List<String> getAgencyList() {
        return agencyList;
    }

    public static void setAgencyList(List<String> agencyList) {
        AgencyType.agencyList = agencyList;
    }
}
