package com.sogou.pay.service.payment.manager;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.common.types.ResultBean;
import com.sogou.pay.manager.model.ChannelAdaptModel;
import com.sogou.pay.manager.payment.ChannelAdaptManager;
import com.sogou.pay.service.BaseTest;

/**
 * Created by wujingpan on 2015/3/9.
 */
public class ChannelAdaptManagerTest extends BaseTest {

    @Autowired
    private ChannelAdaptManager manager;
 
    @Test
    public void testQuery(){
        ResultBean<ChannelAdaptModel> result = manager.getChannelAdapt(1, 1);
        ChannelAdaptModel model = result.getValue();
        System.out.println(model.toString());
    }
}
