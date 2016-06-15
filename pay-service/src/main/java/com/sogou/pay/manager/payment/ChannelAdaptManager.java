package com.sogou.pay.manager.payment;

import com.sogou.pay.common.types.ResultBean;
import com.sogou.pay.manager.model.PayChannelAdapts;

/**
 * @Author	wujingpan
 * @ClassName	ChannelAdaptManager
 * @Date	2015年2月28日
 * @Description:渠道适配服务
 */
public interface ChannelAdaptManager {

    /**
     * 获得收银台银行适配列表
     * @param appId 业务平台ID
     * @param accessPlatform 接入平台 1：PC  2：移动
     * @return 适配列表
     */
    public ResultBean<PayChannelAdapts> getChannelAdapts(Integer appId, Integer accessPlatform);
}
