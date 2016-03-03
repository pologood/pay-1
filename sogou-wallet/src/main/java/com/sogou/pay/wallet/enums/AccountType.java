package com.sogou.pay.wallet.enums;

/**
 * Created by xiepeidong on 2016/3/1.
 */
public enum AccountType implements ValueEnum{
    BALANCE(0),
    LUCKY(1);

    private int value;

    AccountType(int value) {this.value=value;}

    public int getValue(){return this.value;}

    public void setValue(int value) {this.value=value;}
}
