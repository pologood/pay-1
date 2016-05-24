package com.sogou.pay.timer;

import com.sogou.pay.PayPlatformBizServiceLocator;
import com.sogou.pay.common.annotation.Load;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.manager.job.PayTranferTicketRefundQueryJob;
import com.sogou.pay.manager.job.PayTransferQueryJob;
import com.sogou.pay.manager.job.PayTransferRequestJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Created by qibaichao on 2015/6/2.
 * 代付任务
 */
public class PayTransferJob {

    private static final Logger logger = LoggerFactory.getLogger(PayTransferJob.class);

    private static String DATE_FORMAT = "yyyyMMdd";

    @Load(locator = PayPlatformBizServiceLocator.class)
    private PayTransferRequestJob payTransferRequestJob;

    @Load(locator = PayPlatformBizServiceLocator.class)
    private PayTransferQueryJob payTransferQueryJob;

    @Load(locator = PayPlatformBizServiceLocator.class)
    private PayTranferTicketRefundQueryJob payTranferTicketRefundQueryJob;

    /**
     * 代付提交任务(向银行正式发起付款申请)
     */
    public void doRequestJob() {

        try {
            logger.info("代付提交任务begin");
            payTransferRequestJob.doProcessor();
            logger.info("代付提交任务 end");
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("代付提交任务异常：" + ex.getMessage());
        }
    }

    /**
     * 代付查询任务(向银行查询付款申请的结果)
     */
    public void doQueryJob() {

        try {
            logger.info("代付查询任务 begin");
            payTransferQueryJob.doProcessor();
            logger.info("代付查询任务 end");
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("代付查询任务异常：" + ex.getMessage());
        }
    }

    /**
     * 代付查询任务(向银行查询退票的原因)
     */
    public void doRefundQueryJob() {

        try {
            logger.info("代付退票查询任务 begin");
            payTranferTicketRefundQueryJob.doQuery(getYesterday(), getDay());
            logger.info("代付退票查询任务 end");
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("代付查退票询任务异常：" + ex.getMessage());
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
