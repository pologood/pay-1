package com.sogou.pay.service.dao;

import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.Channel;

/**
 * @User: huangguoqing
 * @Date: 2015/03/02
 * @Description: 渠道信息Dao
 */
@Repository
public interface ChannelDao {

    public int insertChannel(Channel channel);

    public Channel selectChannelByCode(String channelCode);

}