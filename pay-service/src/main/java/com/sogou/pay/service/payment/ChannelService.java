package com.sogou.pay.service.payment;

import com.sogou.pay.service.entity.Channel;

/**
 * @User: huangguoqing
 * @Date: 2015/03/02
 * @Description: 渠道服务
 */
public interface ChannelService {
    /**
     * 插入渠道信息
     * @param channel 渠道实体
     * @return 返回值
     */
    public int insertChannel(Channel channel);

    /**
     * 根据渠道编码查询渠道信息
     * @param channelCode 渠道编码
     * @return 渠道实体
     */
    public Channel selectChannelByCode(String channelCode);
}
