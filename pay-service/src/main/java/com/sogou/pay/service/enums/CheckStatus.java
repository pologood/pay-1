package com.sogou.pay.service.enums;


public enum CheckStatus {

    INIT(0), SUCCESS(1), UNBALANCE(2), LOST(3);

    private final int value;

    private CheckStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static CheckStatus findByValue(int value) {
        switch (value) {
            case 0:
                return INIT;
            case 1:
                return SUCCESS;
            case 2:
                return UNBALANCE;
            case 3:
                return LOST;
            default:
                return null;
        }
    }

}
