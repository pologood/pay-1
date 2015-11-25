package com.sogou.pay.notify;

import com.sogou.pay.notify.dao.NotifyErrorLogDao;
import com.sogou.pay.notify.entity.NotifyErrorLog;
import com.sogou.pay.notify.job.NotifyPayJob;
import com.sogou.pay.notify.service.impl.NotifyServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by qibaichao on 2015/4/9.
 */
public class NotifyPayJobTest extends BaseTest {

    @Autowired
    private NotifyPayJob notifyPayJob;

    @Autowired
    private NotifyErrorLogDao notifyErrorLogDao;

    @Autowired
    private NotifyServiceImpl notifyService;

    @Test
    public void doJob() {
        notifyPayJob.doJob();
    }

    @Test
    public void doJobById() {

        NotifyErrorLog notifyErrorLog=notifyErrorLogDao.queryById(48L);
        notifyService.scheduledNotify(notifyErrorLog);
    }


}
