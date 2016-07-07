package com.sogou.pay.service.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayChannel;

import java.util.List;

/**
 * 渠道信息Dao
 */
@Repository
public interface PayChannelDao {

    public PayChannel selectChannelByCode(@Param("channelCode")String channelCode,
                                          @Param("accessPlatform")Integer accessPlatform);

    public List<PayChannel> getCashierChannels(@Param("appId")Integer appId,
                                               @Param("accessPlatform")Integer accessPlatform);

    public PayChannel routeChannel(@Param("appId")Integer appId,
                                   @Param("channelCode")String channelCode,
                                   @Param("accessPlatform")Integer accessPlatform);

}