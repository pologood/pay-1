package com.sogou.pay.service.dao;

import com.sogou.pay.service.entity.PayChannel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.BaseTest;

/**
 * Created by wujingpan on 2015/3/2.
 */
public class PayChannelDaoTest extends BaseTest {

    @Autowired
    PayChannelDao dao;

    @Test
    public void selectByCode(){
        PayChannel c = dao.selectChannelByCode("ABC", 1);
        System.out.println(c.getChannelName());
    }
}
