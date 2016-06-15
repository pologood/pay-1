package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.manager.model.PayChannelAdapt;

import java.util.List;

/**
 * Created by wujingpan on 2015/3/5.
 */
public interface PayChannelAdaptService {

    /**
     * 获得收银台银行适配列表
     * @param appId 业务平台ID
     * @param accessPlatform 接入平台 1：PC  2：移动
     * @return 适配列表
     */
    public List<PayChannelAdapt> getChannelAdaptList(Integer appId, Integer accessPlatform) throws ServiceException;
}
