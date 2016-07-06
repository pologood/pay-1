package com.sogou.pay.service.utils;

import com.sogou.pay.common.utils.SequenceGenerator;
import org.springframework.stereotype.Service;

/**
 * 代付单号
 */
@Service
public class PayTransferNo extends SequenceGenerator {
    private static final String DATA_FORMAT = "yyyyMMddHHmmss";

    protected String getRandom(){
        String str = String.valueOf(System.currentTimeMillis());
        int len = str.length();
        str = str.substring(len-2, len);
        return str;
    }
    @Override
    public String getDateFormat() {
        return DATA_FORMAT;
    }
}
