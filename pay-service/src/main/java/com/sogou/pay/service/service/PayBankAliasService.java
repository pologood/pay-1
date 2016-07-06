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
   *
   * @param agencyCode   第三方支付机构编码
   * @param bankCode     银行编码
   * @param bankCardType 银行卡类型
   * @return 银行别名实体
   */

  public PayBankAlias selectPayBankAlias(String agencyCode, String bankCode, Integer bankCardType) {
    return payBankAliasDao.selectPayBankAlias(agencyCode, bankCode, bankCardType);
  }
}
