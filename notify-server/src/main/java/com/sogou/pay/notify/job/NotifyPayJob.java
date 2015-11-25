/*
 * Copyright 2012-2014 Wanda.cn All right reserved. This software is the
 * confidential and proprietary information of Wanda.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Wanda.cn.
 */
package com.sogou.pay.notify.job;

import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.notify.entity.NotifyErrorLog;
import com.sogou.pay.notify.enums.NotifyStatusEnum;
import com.sogou.pay.notify.enums.NotifyTypeEnum;
import com.sogou.pay.notify.service.NotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * @Author qibaichao
 * @ClassName NotifyPayJobBo
 * @Date 2014年9月18日
 * @Description: 支付成功通知补偿
 * 1.查询状态未通知成功列表
 * 2.通知上限判断，通知或更新为失败
 */
@Service
public class NotifyPayJob extends BatchScheduledJob {

    private static final Logger logger = LoggerFactory.getLogger(NotifyPayJob.class);

    @Autowired
    private NotifyService notifyService;

    /**
     * @Author qibaichao
     * @MethodName doJob
     * @Date 2014年9月22日
     * @Description:任务入口
     */
    public void doJob() {
        doProcessor();
    }

    /**
     * @return
     * @Author qibaichao
     * @MethodName getProcessObjectList
     * @Date 2014年9月22日
     * @Description:获取通知任务
     */
    @Override
    public List<Object> getProcessObjectList() {

        List<NotifyErrorLog> poList = notifyService.queryByNotifyTypeStatus(NotifyTypeEnum.PAY_NOTIFY.value(),
                NotifyStatusEnum.TASK_INIT.value(), new Date());
        return this.castToObjectList(poList);
    }

    /**
     * @param objectList
     * @Author qibaichao
     * @MethodName batchProcess
     * @Date 2014年9月22日
     * @Description:处理通知
     */
    @Override
    public void batchProcess(List<Object> objectList) {
        logger.info("【schduled batch process start size】：" + objectList.size());
        for (Object object : objectList) {
            // 类型判断
            if (object instanceof NotifyErrorLog) {
                NotifyErrorLog notifyErrorLogPo = (NotifyErrorLog) object;
                notifyService.scheduledNotify(notifyErrorLogPo);
            }
        }
        logger.info("【schduled batch process end 】");
        // GC
        objectList = null;
    }

    /**
     * @return
     * @Author qibaichao
     * @MethodName getProcessorName
     * @Date 2014年9月22日
     * @Description:
     */
    @Override
    protected String getProcessorName() {
        return NotifyPayJob.class.getName();
    }
}
