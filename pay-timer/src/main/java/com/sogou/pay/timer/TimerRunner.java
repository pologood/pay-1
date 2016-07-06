package com.sogou.pay.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TimerRunner {

    private static final Logger logger = LoggerFactory.getLogger(TimerRunner.class);

    private static ApplicationContext context;

    public static void main(String[] args) {
        context = new ClassPathXmlApplicationContext("pay_timer.xml");
        logger.info("pay-timer start");
    }
}
