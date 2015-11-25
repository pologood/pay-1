/*
 * Copyright 2014-2016 cyou.cn All right reserved. This software is the
 * confidential and proprietary information of cyou.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cyou.cn.
 */
package com.sogou.pay.notify.enums;

/**
 * @Author qibaichao
 * @ClassName TaskStatusEnum
 * @Date 2014年9月18日
 * @Description:
 */
public enum NotifyStatusEnum {

    /**
     * 通知（pay_notify）:初始
     */
    TASK_INIT(0, "INIT"),
    /**
     * 通知（pay_notify）:成功
     */
    TASK_SUCCESS(1, "SUCCESS"),
    /**
     * 通知（pay_notify）:失败
     */
    TASK_FAIL(2, "FAIL");

    private final int value;

    private final String desc;

    NotifyStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int value() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
