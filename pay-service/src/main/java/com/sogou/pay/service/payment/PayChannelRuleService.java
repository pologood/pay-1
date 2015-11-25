package com.sogou.pay.service.payment;

import com.sogou.pay.service.entity.Channel;
import com.sogou.pay.service.entity.PayChannelRule;

/**
 * @User: huangguoqing
 * @Date: 2015/03/02
 * @Description: 渠道适配规则服务
 */
public interface PayChannelRuleService {

    /**
     * 根据条件查询渠道适配规则信息
     * @param payChannelRule 渠道规则信息
     * @return 渠道实体
     */
    public PayChannelRule selectPayChannelRuleId(PayChannelRule payChannelRule);
}
