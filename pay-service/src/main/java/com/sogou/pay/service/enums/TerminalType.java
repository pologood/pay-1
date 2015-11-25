package com.sogou.pay.service.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hujunfei Date: 15-1-7 Time: 下午4:55
 */
public enum TerminalType {
    UNKNOWN(0),
    WEB(1),
    SDK(2),
    WAP(3);

    private int value;

    TerminalType(int value) {
        this.value = value;
    }

    private static Map<String, TerminalType> mappings = new HashMap();
    private static final TerminalType DEFAULT_TYPE = WEB;

    static {
        mappings.put(WEB.name().toUpperCase(), WEB);
        mappings.put(WAP.name().toUpperCase(), WAP);
        mappings.put(SDK.name().toUpperCase(), SDK);
    }

    public static TerminalType defaultType() {
        return DEFAULT_TYPE;
    }

    /**
     * @param terminal
     * @return 返回相应的TerminalType，查找不到则返回UNKNOWN
     */
    public static TerminalType getType(String terminal) {
        if (terminal == null || terminal.length() == 0) {
            return UNKNOWN;
        }
        TerminalType terminalType = mappings.get(terminal.toUpperCase());
        return terminalType == null ? UNKNOWN : terminalType;
    }

    /**
     * @param terminal
     * @param defaultType
     * @return 返回相应的TerminalType，查找不到则返回defaultType
     */
    public static TerminalType getType(String terminal, TerminalType defaultType) {
        TerminalType type = getType(terminal);
        return type == UNKNOWN ? defaultType : type;
    }

    public int getValue() {
        return value;
    }

    private void setValue(int value) {
        this.value = value;
    }
}
