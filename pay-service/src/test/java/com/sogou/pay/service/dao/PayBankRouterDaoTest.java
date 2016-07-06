package com.sogou.pay.service.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.PayBankRouter;
import com.sogou.pay.service.service.PayBankRouterService;
/**
 * @Author huangguoqing
 * @Date 2015/3/3 18:53
 * @Description:
 */
public class PayBankRouterDaoTest extends BaseTest {
    @Autowired
    private PayBankRouterService payBankRouterService;

    @Autowired
    private PayBankRouterDao dao;
    @Test
    public void selectPayBankRouterList(){
        PayBankRouter router = new PayBankRouter();
        router.setBankCode("ABC");
        router.setBankCardType(1);
        router.setAppId(1999);
        router.setRouterStatus(1);
        List<PayBankRouter> list = payBankRouterService.selectPayBankRouterList(router);
        assertEquals(1,list.size());
    }
    
    @Test
    public void selectCount(){
        
    }
}
