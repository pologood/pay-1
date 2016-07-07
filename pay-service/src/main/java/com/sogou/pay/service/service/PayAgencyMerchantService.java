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

  public PayAgencyMerchant getMerchant(PayAgencyMerchant payAgencyMerchant) {
    return payAgencyMerchantDao.getMerchant(payAgencyMerchant);
  }

  public PayAgencyMerchant getMerchantById(int id) {
    return payAgencyMerchantDao.getMerchantById(id);
  }

  public List<PayAgencyMerchant> getMerchantsByAgencyCode(String agencyCode) {
    return payAgencyMerchantDao.getMerchantsByAgencyCode(agencyCode);
  }

  public PayAgencyMerchant getMerchantByAgencyCodeAndMerchantNo(String agencyCode, String merchantNo) {
    return payAgencyMerchantDao.getMerchantByAgencyCodeAndMerchantNo(agencyCode, merchantNo);
  }

  public List<PayAgencyMerchant> routeMerchants(Integer channelId, Integer appId, Integer companyId) {
    return payAgencyMerchantDao.routeMerchants(channelId, appId, companyId);
  }
}
