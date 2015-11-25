package com.sogou.pay.service.payment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.service.dao.PayResIdDao;
import com.sogou.pay.service.payment.PayResIdService;

/**
 * @Author huangguoqing
 * @Date 2015/4/27 19:11
 * @Description: 支付回调排重业务
 */
@Service
public class PayResIdServiceImpl implements PayResIdService {

    @Autowired
    private PayResIdDao payResIdDao;

    @Override
    public int insertPayResId(String payDetailId) throws Exception {
        return payResIdDao.insertPayResId(payDetailId);
    }
}
