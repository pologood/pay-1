
package com.sogou.pay.timer.transfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 批处理定时任务
 */
@Service
public abstract class BatchScheduledJob {

  private static final Logger logger = LoggerFactory.getLogger(BatchScheduledJob.class);

  protected void doAction() {
    List<Object> objectList = getProcessObjectList();
    if (isStop(objectList)) {
      return;
    }
    batchProcess(objectList);
    objectList = null;
  }

  /**
   * Checks if is stop.
   */
  public boolean isStop(List<Object> objectList) {
    if (null == objectList || objectList.isEmpty()) {
      return true;
    }
    return false;
  }

  /**
   * Gets the process object list.
   */
  public abstract List<Object> getProcessObjectList();

  /**
   * Batch process.
   */
  public abstract void batchProcess(List<Object> objectList);

  /**
   * Cast to object list.
   */
  public List<Object> castToObjectList(List<?> list) {
    List<Object> objectList = new ArrayList<Object>();
    objectList.addAll(list);
    return objectList;
  }

  public void doProcessor() throws Exception {
    long startTime = System.currentTimeMillis();
    logger.info(getProcessorName() + " start.");

    doAction();

    logger.info(getProcessorName() + " end.");
    long endTime = System.currentTimeMillis();
    logger.info(getProcessorName() + " cost " + (endTime - startTime) + " milliseconds");
  }


  /**
   * 任务名称
   */
  protected abstract String getProcessorName();

}
