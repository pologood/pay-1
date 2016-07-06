package com.sogou.pay.service;

import com.sogou.pay.BaseTest;
import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.service.PayChannelAdaptService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wujingpan on 2015/3/5.
 */
public class ChannelAdaptService extends BaseTest {

    @Autowired
    private PayChannelAdaptService service;

    @Test
    public void testQueryAll() throws ServiceException {
        System.out.println(service.getChannelAdaptList(1, 1));
    }
}
