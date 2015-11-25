package com.sogou.pay.notify.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author qibaichao
 * @ClassName AbstractScheduledJob
 * @Date 2014年9月18日
 * @Description:
 *               定时任务抽象基类
 */
@Service
public abstract class AbstractScheduledJob {

    private static final Logger logger = LoggerFactory.getLogger(AbstractScheduledJob.class);

    /**
     * @Author qibaichao
     * @MethodName doProcessor
     * @Date 2014年9月22日
     * @Description:
     */
    public void doProcessor() {
        long startTime = System.currentTimeMillis();
         logger.info(getProcessorName() + " start.");

        doAction();

        logger.info(getProcessorName() + " end.");
        long endTime = System.currentTimeMillis();
        logger.info(getProcessorName() + " cost " + (endTime - startTime) + " milliseconds");
    }

    /**
     * @Author qibaichao
     * @MethodName doAction
     * @Date 2014年9月18日
     * @Description:Do Job
     */
    protected abstract void doAction();

    /**
     * @Author qibaichao
     * @MethodName getProcessorName
     * @return
     * @Date 2014年9月18日
     * @Description:任务名称
     */
    protected abstract String getProcessorName();
}
