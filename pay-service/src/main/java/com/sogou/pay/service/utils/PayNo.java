package com.sogou.pay.service.utils;

import com.sogou.pay.common.utils.SequenceGenerator;
import org.springframework.stereotype.Service;


/**
 * 支付单号
 */
@Service
public class PayNo extends SequenceGenerator {

    private static final String DATA_FORMAT = "yyyyMMddHHmmss";

    @Override
    public String getDateFormat() {
        return DATA_FORMAT;
    }
}
