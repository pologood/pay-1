package com.sogou.pay.service.utils;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.service.BaseTest;
import com.sogou.pay.service.utils.orderNoGenerator.SequencerGenerator;

public class GenOrderNOTest extends BaseTest {

    @Autowired
    SequencerGenerator rrr;

    @Test
    public void test() {
        // test
        System.out.println(rrr.getPayDetailId());
        System.out.println(rrr.getPayDetailId());
        System.out.println(rrr.getPayId());
        System.out.println(rrr.getRefundDetailId());
        System.out.println();

        System.out.println();

    }
}
