package com.sogou.pay.manager.job;

import com.sogou.pay.manager.payment.PayTransferQueryManager;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.enums.PayTransferBatchStatus;
import com.sogou.pay.service.payment.PayTransferBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qibaichao on 2015/6/2.
 * 代发查询任务
 */
@Component
public class PayTransferQueryJob extends BatchScheduledJob {

    private static final Logger logger = LoggerFactory.getLogger(PayTransferQueryJob.class);

    @Autowired
    private PayTransferQueryManager payTransferQueryManager;

    @Autowired
    private PayTransferBatchService payTransferBatchService;

    @Override
    public List<Object> getProcessObjectList() {
        List<PayTransferBatch> payTransferBatchList = null;
        try {
            // 查询交易状态为处理中的
            payTransferBatchList = payTransferBatchService.queryByTradeStatus(PayTransferBatchStatus.IN_PROCESSING.getValue());
            int size = payTransferBatchList.size();
            logger.info("【代付查询job】 size:" + size);
            if (size != 0) {
                return this.castToObjectList(payTransferBatchList);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    @Override
    public void batchProcess(List<Object> objectList) {

        for (Object object : objectList) {
            if (object instanceof PayTransferBatch) {
                PayTransferBatch payTransferBatch = (PayTransferBatch) object;
                payTransferQueryManager.doProcess(payTransferBatch);
            }
        }
    }

    @Override
    protected String getProcessorName() {
        return PayTransferQueryJob.class.getName();
    }
}
