package com.sogou.pay.service.logback;

import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by qibaichao on 2015/10/23.
 */
public class Perf4JAppenderExample {

    private static final Logger log = LoggerFactory.getLogger("dbTimingLogger");

//    private final static Logger log = LoggerFactory.getLogger(Perf4JAppenderExample.class);

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 10; i++) {

            // Log4JStopWatch默认使用org.perf4j.TimingLogger这个类

            Slf4JStopWatch stopWatch =  new Slf4JStopWatch("secondBlock",log);

            //模拟代码运行时间

            Thread.sleep((long) (Math.random() * 1000L));

            //打印到控制台

            log.info("Normal logging messages only go to the console");

            stopWatch.lap("firstBlock");

            Thread.sleep((long) (Math.random() * 2000L));

            stopWatch.stop("secondBlock");

        }
    }
}
