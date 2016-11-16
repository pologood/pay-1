package com.sogou.pay.notify.job;

import com.sogou.pay.notify.entity.NotifyToDo;
import com.sogou.pay.notify.enums.NotifyType;
import com.sogou.pay.notify.service.NotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 支付成功通知补偿
 * 1.查询状态未通知成功列表
 * 2.通知上限判断，通知或更新为失败
 */
@Service
public class PayNotifyJob extends BatchScheduledJob {

    private static final Logger logger = LoggerFactory.getLogger(PayNotifyJob.class);

    @Autowired
    private NotifyService notifyService;

    public void doJob() {
        doProcessor();
    }

    @Override
    public List<Object> getProcessObjectList() {

        List<NotifyToDo> poList = notifyService.getNotifyToDo(NotifyType.PAY_NOTIFY.value());
        return this.castToObjectList(poList);
    }

    @Override
    public void batchProcess(List<Object> objectList) {
        logger.info("[batchProcess] begin, objectList.size={}",objectList.size());
        for (Object object : objectList) {
            // 类型判断
            if (object instanceof NotifyToDo) {
                NotifyToDo notifyToDoPo = (NotifyToDo) object;
                notifyService.scheduleNotify(notifyToDoPo);
            }
        }
        logger.info("[batchProcess] finish");
        // GC
        objectList = null;
    }

    @Override
    protected String getProcessorName() {
        return PayNotifyJob.class.getName();
    }
}
