package com.sogou.pay.service.payment.impl;

import com.sogou.pay.service.dao.ChannelDao;
import com.sogou.pay.service.entity.Channel;
import com.sogou.pay.service.payment.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @User: huangguoqing
 * @Date: 2015/03/02
 * @Description: 渠道服务
 */
@Service
public class ChannelServiceImpl implements ChannelService {
    @Autowired
    private ChannelDao channelDao;

    /**
     * 插入渠道信息
     * @param channel 渠道实体
     * @return 是否成功标识
     */
    @Override
    public int insertChannel(Channel channel) {
        return channelDao.insertChannel(channel);
    }

    /**
     * 根据渠道编码查询渠道信息
     * @param channelCode 渠道编码
     * @return 渠道信息
     */
    @Override
    public Channel selectChannelByCode(String  channelCode) {
        return  channelDao.selectChannelByCode(channelCode);
    }
}
