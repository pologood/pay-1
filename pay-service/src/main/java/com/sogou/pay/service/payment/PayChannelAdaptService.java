package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.manager.model.PayChannelAdapt;
import com.sogou.pay.service.dao.ChannelAdaptDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PayChannelAdaptService {

  @Autowired
  private ChannelAdaptDao dao;


  public List<PayChannelAdapt> getChannelAdaptList(Integer appId, Integer accessPlatform) throws ServiceException {
    return dao.getChannelAdaptList(appId, accessPlatform);
  }
}
