package com.sogou.pay.service.utils;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.BaseTest;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;

public class GenOrderNOTest extends BaseTest {

    @Autowired
    SequenceFactory rrr;

    @Test
    public void test() {
        // test
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++){
                    System.out.println(rrr.getPayDetailId());
                    System.out.println(rrr.getPayId());
                    System.out.println(rrr.getRefundDetailId());
                    System.out.println();
                }
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++){
                    System.out.println(rrr.getPayDetailId());
                    System.out.println(rrr.getPayId());
                    System.out.println(rrr.getRefundDetailId());
                    System.out.println();
                }
            }
        };
        thread1.start();
        thread2.start();
        for (int i = 0; i < 10; i++){
            System.out.println(rrr.getPayDetailId());
            System.out.println(rrr.getPayId());
            System.out.println(rrr.getRefundDetailId());
            System.out.println();
        }
        try {
            thread1.join();
            thread2.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
