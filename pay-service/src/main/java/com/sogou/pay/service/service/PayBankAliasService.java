package com.sogou.pay.service.service;

import com.sogou.pay.service.dao.PayBankAliasDao;
import com.sogou.pay.service.entity.PayBankAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PayBankAliasService {

  @Autowired
  private PayBankAliasDao payBankAliasDao;

  /**
   * 获取银行别名
   */

  public PayBankAlias selectPayBankAlias(String agencyCode, String bankCode) {
    return payBankAliasDao.selectPayBankAlias(agencyCode, bankCode);
  }
}
