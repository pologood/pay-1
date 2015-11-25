package com.sogou.pay.service.utils.orderNoGenerator;

import org.springframework.stereotype.Service;

/**
 * Created by qibaichao on 2015/6/2.
 * 代付单批次号
 */
@Service
public class PayTransferBatchNo extends AbstractSequence {
    private static final String DATA_FORMAT = "yyyyMMddHHmmss";

    @Override
    public String getDateFormat() {
        return DATA_FORMAT;
    }

    @Override
    public int getSecondPartLength() {
        return 3;
    }

}
