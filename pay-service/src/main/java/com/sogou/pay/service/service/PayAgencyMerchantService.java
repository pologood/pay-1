package com.sogou.pay.service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.service.dao.PayAgencyMerchantDao;
import com.sogou.pay.service.entity.PayAgencyMerchant;


@Service
public class PayAgencyMerchantService {

  @Autowired
  private PayAgencyMerchantDao payAgencyMerchantDao;


  public PayAgencyMerchant selectPayAgencyMerchant(PayAgencyMerchant payAgencyMerchant) {
    return payAgencyMerchantDao.selectPayAgencyMerchant(payAgencyMerchant);
  }


  public PayAgencyMerchant selectPayAgencyMerchantById(int id) {
    return payAgencyMerchantDao.selectPayAgencyMerchantById(id);
  }


  public List<PayAgencyMerchant> selectPayAgencyMerchants(String agencyCode) {
    return payAgencyMerchantDao.selectPayAgencyMerchants(agencyCode);
  }


  public PayAgencyMerchant selectByAgencyAndMerchant(String agencyCode, String merchantNo) {
    return payAgencyMerchantDao.selectByAgencyAndMerchant(agencyCode, merchantNo);
  }
}
