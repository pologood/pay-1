package com.sogou.pay.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.service.dao.PayChannelRuleDao;
import com.sogou.pay.service.entity.PayChannelRule;


@Service
public class PayChannelRuleService {
  @Autowired
  private PayChannelRuleDao payChannelDao;

  /**
   * 根据条件查询渠道适配规则
   *
   * @param payChannelRule 渠道规则信息
   * @return 渠道实体
   */

  public PayChannelRule selectPayChannelRuleId(PayChannelRule payChannelRule) {
    return payChannelDao.selectPayChannelRule(payChannelRule);
  }
}
