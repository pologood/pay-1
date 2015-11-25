/*
 * Copyright 2012-2014 Wanda.cn All right reserved. This software is the
 * confidential and proprietary information of Wanda.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Wanda.cn.
 */
package com.sogou.pay.manager.job;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类BatchScheduledJob.java的实现描述: 批处理定时任务
 *
 * @author qibaichao 2015-06-02 下午3:25:39
 */
@Service
public abstract class BatchScheduledJob extends AbstractScheduledJob {

    private static final Logger logger = LoggerFactory.getLogger(BatchScheduledJob.class);

    @Override
    protected void doAction() throws Exception {

        /**
         * 初始化
         */
        init();

//        while (true) {

            /**
             * 分批次获取数据
             */
            List<Object> objectList = getProcessObjectList();

            /**
             * 结束判断
             */
            if (isStop(objectList)) {
                return;
            }

            /**
             * 批量处理
             */
            batchProcess(objectList);

            /**
             * GC
             */
            objectList = null;
//        }

        /**
         * 任务结束，清理内存
         */
        finish();

    }

    /**
     * Init 生成查询对象.
     */
    public void init() {

    }

    /**
     * 任务结束
     */
    public void finish() {

    }

    /**
     * Checks if is stop.
     *
     * @return true, if is stop
     */
    public boolean isStop(List<Object> objectList) {
        /**
         * 当对象为空时，停止处理
         */
        if (null == objectList || objectList.isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * Gets the process object list.
     *
     * @return the process object list
     */
    public abstract List<Object> getProcessObjectList() throws Exception;

    /**
     * Batch process.
     *
     * @param objectList the update object list
     */
    public abstract void batchProcess(List<Object> objectList) throws Exception;

    /**
     * Cast to object list.
     *
     * @param list the list
     * @return the list
     */
    public List<Object> castToObjectList(List<?> list) {

        List<Object> objectList = new ArrayList<Object>();
        objectList.addAll(list);

        return objectList;
    }

}
