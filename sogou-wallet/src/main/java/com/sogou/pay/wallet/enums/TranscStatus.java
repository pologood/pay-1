package com.sogou.pay.wallet.enums;

import com.sogou.pay.common.enums.ValueEnum;

/**
 * Created by xiepeidong on 2016/2/26.
 */
public enum TranscStatus implements ValueEnum {
    INIT(0),
    SUCCESS(1),
    FAIL(2);

    private int value;

    TranscStatus(int value) {this.value=value;}

    public int getValue(){return this.value;}

    public void setValue(int value) {this.value=value;}
}
