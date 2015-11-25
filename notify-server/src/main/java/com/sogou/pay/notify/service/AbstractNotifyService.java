package com.sogou.pay.notify.service;

import com.sogou.pay.notify.entity.NotifyErrorLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author qibaichao
 * @ClassName AbstractPayNotifyService
 * @Date 2014年9月18日
 * @Description:通知抽象类
 */
@Service
public abstract class AbstractNotifyService implements NotifyService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractNotifyService.class);

    /**
     * 单个CPU线程池大小
     */
    protected final int POOL_SIZE = 20;

    /**
     * 线程池
     */
    protected ExecutorService notifyExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime()
            .availableProcessors() * POOL_SIZE);

    @Override
    public void firstNotify(final int type, final String payId, final String notifyUrl, final Map<String, String> notifyParam) {
        // 执行线程
        notifyExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                doFirstNotify(type, payId, notifyUrl, notifyParam);
            }
        });
    }

    @Override
    public void scheduledNotify(NotifyErrorLog notifyErrorLog) {
        doSchduledNotify(notifyErrorLog);
    }

    /**
     * @return
     * @Author qibaichao
     * @MethodName doFirstNotify
     * @Date 2014年9月18日
     * @Description:子类具体实现
     */
    public abstract void doFirstNotify(int type, String payId, String notifyUrl, Map<String, String> notifyParam);

    /**
     * @param notifyErrorLogPo
     * @Author qibaichao
     * @MethodName doSchduledNotify
     * @Date 2014年9月18日
     * @Description:子类具体实现
     */
    public abstract void doSchduledNotify(NotifyErrorLog notifyErrorLogPo);

}
