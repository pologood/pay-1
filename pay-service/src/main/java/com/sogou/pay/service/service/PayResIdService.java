package com.sogou.pay.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.service.dao.PayResIdDao;

@Service
public class PayResIdService {

    @Autowired
    private PayResIdDao payResIdDao;

    
    public int insertPayResId(String payDetailId) throws Exception {
        return payResIdDao.insertPayResId(payDetailId);
    }
}
