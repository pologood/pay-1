package com.sogou.pay.notify.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NotifyRunner {

    private static final Logger logger = LoggerFactory.getLogger(NotifyRunner.class);
    private static ApplicationContext context;

    public static void main(String[] args) {
        context = new ClassPathXmlApplicationContext("notify_server.xml");
        logger.info("notify-server start");
    }
}
