package com.sogou.pay.service.payment;

import com.sogou.pay.service.dao.PayBankRouterDao;
import com.sogou.pay.service.entity.PayBankRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PayBankRouterService {

  @Autowired
  private PayBankRouterDao payBankRouterDao;

  public List<PayBankRouter> selectPayBankRouterList(PayBankRouter payBankRouter) {
    return payBankRouterDao.selectPayBankRouterList(payBankRouter);
  }
}
