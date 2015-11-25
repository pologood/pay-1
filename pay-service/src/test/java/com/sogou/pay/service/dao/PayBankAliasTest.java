package com.sogou.pay.service.dao;

import com.sogou.pay.service.BaseTest;
import com.sogou.pay.service.entity.PayBankAlias;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author huangguoqing
 * @Date 2015/3/9 10:56
 * @Description:
 */
public class PayBankAliasTest extends BaseTest {
    @Autowired
    PayBankAliasDao dao;

    @Test
    public void selectTest(){
        PayBankAlias a = dao.selectPayBankAlias("ALIPAY","ABC",2);
        assertEquals("ABC",a.getAliasName());
    }
}
