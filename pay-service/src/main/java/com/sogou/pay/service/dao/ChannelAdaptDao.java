package com.sogou.pay.service.dao;

import com.sogou.pay.service.model.PayChannelAdapt;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wujingpan on 2015/3/5.
 */
@Repository
public interface ChannelAdaptDao {

    /**
     * 获得收银台银行适配列表
     * @param appId 业务平台ID
     * @param accessPlatform 接入平台 1：PC  2：移动
     * @return 适配列表
     */
    public List<PayChannelAdapt> getChannelAdaptList(@Param("appId")Integer appId, @Param("accessPlatform")Integer
            accessPlatform);
}
