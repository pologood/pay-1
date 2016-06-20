package com.sogou.pay.timer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:*.xml")
public class BaseTest extends Assert {
    @Before
    public void baseBefore() {
        System.out.println("-----------开始测试用例-----------");
    }

    @After
    public void baseAfter() {
        System.out.println("-----------结束测试用例----------");
    }
}
