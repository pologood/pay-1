package com.sogou.pay.service.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.Channel;

/**
 * Created by wujingpan on 2015/3/2.
 */
public class ChannelDaoTest extends BaseTest {

    @Autowired
    ChannelDao dao;

    @Test
    public void selectByCode(){
        Channel c = dao.selectChannelByCode("ABC", 1);
        System.out.println(c.getChannelName());
    }
}
