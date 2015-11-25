package com.sogou.pay.service.payment.impl;

import com.sogou.pay.service.dao.PayBankRouterDao;
import com.sogou.pay.service.entity.PayBankRouter;
import com.sogou.pay.service.payment.PayBankRouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author huangguoqing
 * @Date 2015/3/3 17:35
 * @Description: 银行路由业务
 */
@Service
public class PayBankRouterServiceImpl implements PayBankRouterService{

    @Autowired
    private PayBankRouterDao payBankRouterDao;
    @Override
    public List<PayBankRouter> selectPayBankRouterList(PayBankRouter payBankRouter) {
        return payBankRouterDao.selectPayBankRouterList(payBankRouter);
    }
}
