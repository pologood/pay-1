package com.sogou.pay.service;

import java.math.BigDecimal;

import com.sogou.pay.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.service.payment.PayFeeService;

/**
 * Created by wujingpan on 2015/3/5.
 */
public class PayFeeServiceTest extends BaseTest {

    @Autowired
    private PayFeeService service;

    @Test
    public void getFee(){
        try {
            PMap<String,BigDecimal> map = service.getPayFee(BigDecimal.valueOf(0.01),"2088811923135335",2,2);
            System.out.println(map.get("feeRate"));
            System.out.println(map.get("fee"));
            //assertEquals(fee, BigDecimal.valueOf(0.30));
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
