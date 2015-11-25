package com.sogou.pay.thirdpay;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hujunfei Date: 14-12-30 Time: 下午4:48
 */
public class LogbackTest extends BaseTest {

    private final static Logger log = LoggerFactory.getLogger(LogbackTest.class);

    @Test
    public void testLog() {
        for (int i = 0; i < 100; i++) {
            log.trace("======trace");
            log.debug("======debug");
            log.info("======info");
            log.warn("======warn");
            log.error("======error");
        }
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) {
        log.info("======info");
        log.error("======error");
    }
}
