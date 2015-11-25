package com.sogou.pay.service.payment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.service.dao.PayChannelRuleDao;
import com.sogou.pay.service.entity.PayChannelRule;
import com.sogou.pay.service.payment.PayChannelRuleService;

/**
 * @User: huangguoqing
 * @Date: 2015/03/02
 * @Description: 渠道服务
 */
@Service
public class PayChannelRuleServiceImpl implements PayChannelRuleService {
    @Autowired
    private PayChannelRuleDao payChannelDao;

    /**
     * 根据条件查询渠道适配规则
     * @param payChannelRule 渠道规则信息
     * @return 渠道实体
     */
    @Override
    public PayChannelRule selectPayChannelRuleId(PayChannelRule payChannelRule) {
        return  payChannelDao.selectPayChannelRule(payChannelRule);
    }
}
