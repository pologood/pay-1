package com.sogou.pay.timer.transfer;

import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.common.enums.PayTransferBatchStatus;
import com.sogou.pay.service.payment.PayTransferBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class TransferJob extends BatchScheduledJob {

  private static final Logger logger = LoggerFactory.getLogger(TransferJob.class);

  @Autowired
  private TransferManager transferManager;

  @Autowired
  private PayTransferBatchService payTransferBatchService;

  @Override
  public List<Object> getProcessObjectList() {
    List<PayTransferBatch> payTransferBatchList = null;
    try {
      // 查询交易状态为处理中的
      payTransferBatchList = payTransferBatchService
              .queryByTradeStatus(PayTransferBatchStatus.FINAL_APPROVED.getValue());
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
  public void batchProcess(List<Object> objectList) {
    for (Object object : objectList) {
      if (object instanceof PayTransferBatch) {
        PayTransferBatch payTransferBatch = (PayTransferBatch) object;
        transferManager.transfer(String.valueOf(payTransferBatch.getAppId()),
                payTransferBatch.getBatchNo());
      }
    }
  }

  protected String getProcessorName() {
    return TransferJob.class.getName();
  }
}
