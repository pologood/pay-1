package com.sogou.pay.notify;

import com.sogou.pay.notify.dao.NotifyToDoDao;
import com.sogou.pay.notify.entity.NotifyToDo;
import com.sogou.pay.notify.job.PayNotifyJob;
import com.sogou.pay.notify.service.NotifyService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class NotifyPayJobTest extends BaseTest {

    @Autowired
    private PayNotifyJob notifyPayJob;

    @Autowired
    private NotifyToDoDao notifyToDoDao;

    @Autowired
    private NotifyService notifyService;

    @Test
    public void doJob() {
        notifyPayJob.doJob();
    }

    @Test
    public void doJobById() {

        NotifyToDo notifyToDo = notifyToDoDao.queryById(439L);
        notifyService.scheduleNotify(notifyToDo);
    }


}
