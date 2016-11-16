package com.sogou.pay.service.service;

import com.sogou.pay.service.dao.PayChannelDao;
import com.sogou.pay.service.entity.PayChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PayChannelService {
  @Autowired
  private PayChannelDao payChannelDao;

  public List<PayChannel> getCashierChannels(Integer appId, Integer accessPlatform) {
    return payChannelDao.getCashierChannels(appId, accessPlatform);
  }

  public PayChannel routeChannel(Integer appId, String channelCode, Integer accessPlatform) {
    return payChannelDao.routeChannel(appId, channelCode, accessPlatform);
  }
}
