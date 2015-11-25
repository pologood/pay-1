package com.sogou.pay.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import com.sogou.pay.manager.payment.impl.PayManagerImpl;
import com.sogou.pay.service.payment.PayResIdService;

public class PayResIdServiceTest extends BaseTest{

    @Autowired
    private PayResIdService payResIdService;
    private static final Logger logger = LoggerFactory.getLogger(PayResIdServiceTest.class);
    @Test
    public void insertTest(){
        try{
            payResIdService.insertPayResId("11");
        }catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
        } catch (Exception e){
            System.out.println("sss");
        }
    }
}
