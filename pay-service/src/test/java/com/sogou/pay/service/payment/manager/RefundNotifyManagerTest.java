package com.sogou.pay.service.payment.manager;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.manager.notify.RefundNotifyManager;
import com.sogou.pay.service.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by qibaichao on 2015/4/20.
 */
public class RefundNotifyManagerTest extends BaseTest {

    @Autowired
    private RefundNotifyManager refundNotifyManager;

    @Test
    public void repairRefundOrder() {
        String refundId = "20150417153642747001";
        String thirdRefundId = "2015041600001000370048486386";
        try {
            Result result =  refundNotifyManager.repairRefundOrder(refundId, thirdRefundId);
            System.out.println(JSONUtil.Bean2JSON(result));
        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

}
