/*
 * Copyright 2012-2014 Wanda.cn All right reserved. This software is the
 * confidential and proprietary information of Wanda.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Wanda.cn.
 */
package com.sogou.pay.manager.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类AbstractScheduledProcessor.java的实现描述：定时任务抽象基类
 *
 * @author qibaichao 2015-06-02 下午3:25:39
 */
@Service
public abstract class AbstractScheduledJob<T> {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractScheduledJob.class);

    public void doProcessor() throws Exception{
        long startTime = System.currentTimeMillis();
        logger.info(getProcessorName() + " start.");

        doAction();

        logger.info(getProcessorName() + " end.");
        long endTime = System.currentTimeMillis();
        logger.info(getProcessorName() + " cost " + (endTime - startTime) + " milliseconds");
    }

    /**
     * Do Job.
     */
    protected abstract void doAction()throws Exception;

    /**
     * 任务名称
     *
     * @return
     */
    protected abstract String getProcessorName();
}
