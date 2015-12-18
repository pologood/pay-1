package com.sogou.pay.service.utils.orderNoGenerator;

import org.springframework.stereotype.Service;

/**
 * Created by qibaichao on 2015/6/2.
 * 代付单号
 */
@Service
public class PayTransferNo extends AbstractSequence {
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

    @Override
    public int getSecondPartLength() {
        return 3;
    }
}
