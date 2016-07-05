package com.sogou.pay.notify.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifyRunner {

    private static final Logger logger = LoggerFactory.getLogger(NotifyRunner.class);

    /**
     * timer程序启动方法
     */
    public static void main(String[] args) {

        try {
            NotifyServerLocator.getApplicationContext();
            logger.info("[main] pay-notify start");
            while (System.in.available() == 0) {
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            logger.error("[main] failed, {]", e);
            System.exit(100);
        }
    }
}
