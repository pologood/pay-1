package com.sogou.pay.service.payment;

import com.sogou.pay.service.dao.ChannelDao;
import com.sogou.pay.service.entity.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ChannelService {
  @Autowired
  private ChannelDao channelDao;

  /**
   * 插入渠道信息
   *
   * @param channel 渠道实体
   * @return 是否成功标识
   */

  public int insertChannel(Channel channel) {
    return channelDao.insertChannel(channel);
  }

  /**
   * 根据渠道编码查询渠道信息
   *
   * @param channelCode 渠道编码
   * @return 渠道信息
   */

  public Channel selectChannelByCode(String channelCode, Integer accessPlatform) {
    return channelDao.selectChannelByCode(channelCode, accessPlatform);
  }
}
