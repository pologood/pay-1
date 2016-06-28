package com.sogou.pay.service.utils.orderNoGenerator;

import com.sogou.pay.common.utils.SequenceGenerator;
import org.springframework.stereotype.Service;

/**
 * 代付单批次号
 */
@Service
public class PayTransferBatchNo extends SequenceGenerator {
    private static final String DATA_FORMAT = "yyyyMMddHHmmss";

    @Override
    public String getDateFormat() {
        return DATA_FORMAT;
    }

}
