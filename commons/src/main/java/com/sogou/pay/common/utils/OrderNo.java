package com.sogou.pay.common.utils;

import org.springframework.stereotype.Service;


/**
 * @Author hgq
 * @Description:订单
 */
@Service
public class OrderNo extends SequenceGenerator {

    private static final String DATA_FORMAT = "yyyyMMddHHmmss";

    @Override
    public String getDateFormat() {
        return DATA_FORMAT;
    }
}
