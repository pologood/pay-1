package com.sogou.pay.service.dao;

import org.apache.ibatis.annotations.Param;
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

    public Channel selectChannelByCode(@Param("channelCode")String channelCode, @Param("accessPlatform")Integer accessPlatform);

}