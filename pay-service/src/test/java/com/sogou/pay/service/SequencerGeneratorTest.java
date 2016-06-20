package com.sogou.pay.service;

import com.sogou.pay.BaseTest;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by qibaichao on 2015/6/2.
 */
public class SequencerGeneratorTest extends BaseTest {


    @Autowired
    private SequenceFactory sequencerGenerator;

    @Test
    public void getPayTransferBatchNo() {
        for(int i=0;i<1000;i++){
            String no = sequencerGenerator.getTransferBatchNo();
            System.out.println(no);
        }
    }
    @Test
    public void getPayTransferNo() {
        String no = sequencerGenerator.getTransferNo();
        System.out.println(no);
        System.out.println(no.length());

    }
}
