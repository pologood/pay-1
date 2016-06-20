package com.sogou.pay.timer.transfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class QueryTransferRefundJob{

    private static final Logger logger = LoggerFactory.getLogger(QueryTransferRefundJob.class);

    @Autowired
    private TransferManager transferManager;

    public void doProcessor(String beginDate, String endDate){
        transferManager.queryTransferRefund(beginDate, endDate);
    }

}
