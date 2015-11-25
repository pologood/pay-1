package com.sogou.pay.service.payment.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.service.dao.PayAgencyMerchantDao;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.payment.PayAgencyMerchantService;

/**
 * @Author huangguoqing
 * @Date 2015/3/4 13:35
 * @Description: 机构商户业务
 */
@Service
public class PayAgencyMerchantServiceImpl implements PayAgencyMerchantService{

    @Autowired
    private PayAgencyMerchantDao payAgencyMerchantDao;

    @Override
    public PayAgencyMerchant selectPayAgencyMerchant(PayAgencyMerchant payAgencyMerchant) {
        return payAgencyMerchantDao.selectPayAgencyMerchant(payAgencyMerchant);
    }

    @Override
    public PayAgencyMerchant selectPayAgencyMerchantById(int id) {
        return payAgencyMerchantDao.selectPayAgencyMerchantById(id);
    }

    @Override
    public List<PayAgencyMerchant> selectPayAgencyMerchants(String agencyCode) {
        return payAgencyMerchantDao.selectPayAgencyMerchants(agencyCode);
    }

    @Override
    public PayAgencyMerchant selectByAgencyAndMerchant(String agencyCode, String merchantNo) {
        return payAgencyMerchantDao.selectByAgencyAndMerchant(agencyCode, merchantNo);
    }
}
