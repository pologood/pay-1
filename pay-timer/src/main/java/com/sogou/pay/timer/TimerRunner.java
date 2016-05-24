package com.sogou.pay.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by qibaichao on 2015/3/11.
 */
public class TimerRunner {

    private static final Logger logger = LoggerFactory.getLogger(TimerRunner.class);
    /**
     * timer程序启动方法
     * @param args
     */
    public static void main(String[] args) {
        PayPlatformTimerServiceLocator.getApplicationContext();
        logger.info("TimerRunner start");
    }
}
