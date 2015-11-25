package com.sogou.pay.notify.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by qibaichao on 2015/3/11.
 */
public class NotifyRunner {

    private static final Logger logger = LoggerFactory.getLogger(NotifyRunner.class);

    /**
     * timer程序启动方法
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            NotifyServerLocator.getApplicationContext();
            logger.info("pay notify start");
            while (System.in.available() == 0) {
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }
}
