package com.sogou.pay.service.dao;

import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayChannelRule;

/**
 * @User: huangguoqing
 * @Date: 2015/03/02
 * @Description: 渠道规则信息Dao
 */
@Repository
public interface PayChannelRuleDao {

    public PayChannelRule selectPayChannelRule(PayChannelRule payChannelRule);

}
