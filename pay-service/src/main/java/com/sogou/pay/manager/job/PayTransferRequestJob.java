package com.sogou.pay.manager.job;

import com.sogou.pay.manager.payment.PayTranferRequestManager;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.enums.PayTransferBatchStatus;
import com.sogou.pay.service.payment.PayTransferBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by qibaichao on 2015/7/3.
 */
@Component
public class PayTransferRequestJob extends BatchScheduledJob {

    private static final Logger logger = LoggerFactory.getLogger(PayTransferRequestJob.class);

    @Autowired
    private PayTranferRequestManager payTranferRequestManager;

    @Autowired
    private PayTransferBatchService payTransferBatchService;

    @Override
    public List<Object> getProcessObjectList() throws Exception {
        List<PayTransferBatch> payTransferBatchList = null;
        try {
            // 查询交易状态为处理中的
            payTransferBatchList = payTransferBatchService.queryByTradeStatus(PayTransferBatchStatus.AUDIT_PASS.getValue());
            int size = payTransferBatchList.size();
            logger.info("【代付请求job】 size:" + size);
            if (size != 0) {
                return this.castToObjectList(payTransferBatchList);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    @Override
    public void batchProcess(List<Object> objectList) throws Exception {
        for (Object object : objectList) {
            if (object instanceof PayTransferBatch) {
                PayTransferBatch payTransferBatch = (PayTransferBatch) object;
                payTranferRequestManager.doProcess(String.valueOf(payTransferBatch.getAppId()),payTransferBatch.getBatchNo());
            }
        }
    }

    protected String getProcessorName() {
        return PayTransferRequestJob.class.getName();
    }
}
