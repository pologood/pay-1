package com.sogou.pay.timer.transfer;

import com.sogou.pay.PayPlatformBizServiceLocator;
import com.sogou.pay.common.annotation.Load;
import com.sogou.pay.common.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * 代付任务
 */
public class TransferTimer {

  private static final Logger logger = LoggerFactory.getLogger(TransferTimer.class);

  private static String DATE_FORMAT = "yyyyMMdd";

  @Load(locator = PayPlatformBizServiceLocator.class)
  private TransferJob transferJob;

  @Load(locator = PayPlatformBizServiceLocator.class)
  private QueryTransferJob queryTransferJob;

  @Load(locator = PayPlatformBizServiceLocator.class)
  private QueryTransferRefundJob queryTransferRefundJob;

  /**
   * 向银行正式发起付款申请
   */
  public void doTransferJob() {

    try {
      logger.info("[doTransferJob] begin");
      transferJob.doProcessor();
      logger.info("[doTransferJob] end");
    } catch (Exception ex) {
      logger.error("[doTransferJob] failed, {}", ex);
    }
  }

  /**
   * 向银行查询付款申请的结果
   */
  public void doQueryTransferJob() {

    try {
      logger.info("[doQueryTransferJob] begin");
      queryTransferJob.doProcessor();
      logger.info("[doQueryTransferJob] end");
    } catch (Exception ex) {
      logger.error("[doQueryTransferJob] failed, {}", ex);
    }
  }

  /**
   * 向银行查询退票的原因
   */
  public void doQueryTransferRefundJob() {

    try {
      logger.info("[doQueryTransferRefundJob] begin");
      queryTransferRefundJob.doProcessor(getYesterday(), getDay());
      logger.info("[doQueryTransferRefundJob] end");
    } catch (Exception ex) {
      logger.error("[doQueryTransferRefundJob] failed, {}", ex);
    }
  }

  private String getYesterday() {

    Calendar ca = Calendar.getInstance();
    ca.add(Calendar.DATE, -1);
    String yesterday = DateUtil.format(ca.getTime(), DATE_FORMAT);
    return yesterday;
  }

  private String getDay() {

    Calendar ca = Calendar.getInstance();
    String yesterday = DateUtil.format(ca.getTime(), DATE_FORMAT);
    return yesterday;
  }
}
