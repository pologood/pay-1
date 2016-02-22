/*
 * Copyright 2012-2014 Wanda.cn All right reserved. This software is the
 * confidential and proprietary information of Wanda.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Wanda.cn.
 */
package com.sogou.pay.service.utils;

import com.sogou.pay.thirdpay.biz.enums.AgencyType;

import java.util.ArrayList;
import java.util.List;

/**
 * 类PaySet.java的实现描述：支付常量集合
 *
 * @author qibaichao 2015-06-03 上午11:52:21
 */
public class PaySet {


    /**
     * 已接通代付方式
     */
    public static final List<AgencyType> IN_SERVICE_TRANSFER_PAYMENTS = new ArrayList<AgencyType>();

    static {

        /**
         * TENPAY
         */
        IN_SERVICE_TRANSFER_PAYMENTS.add(AgencyType.TENPAY);

    }

}
