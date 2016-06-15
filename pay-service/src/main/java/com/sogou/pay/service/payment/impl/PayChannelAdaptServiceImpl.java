package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.manager.model.PayChannelAdapt;
import com.sogou.pay.service.dao.ChannelAdaptDao;
import com.sogou.pay.service.payment.PayChannelAdaptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wujingpan on 2015/3/5.
 */
@Service
public class PayChannelAdaptServiceImpl implements PayChannelAdaptService {

    @Autowired
    private ChannelAdaptDao dao;

        @Override
        public List<PayChannelAdapt> getChannelAdaptList(Integer appId, Integer accessPlatform) throws ServiceException {
            return dao.getChannelAdaptList(appId,accessPlatform);
    }
}
