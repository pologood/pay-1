package com.sogou.pay.manager.payment;

import com.sogou.pay.common.result.Result;
import com.sogou.pay.service.entity.App;

/**
 * @Author	huangguoqing 
 * @ClassName	AppManager 
 * @Date	2015年4月22日 
 * @Description:业务平台manager
 */
public interface AppManager {

    /**
     * @Author	huangguoqing 
     * @MethodName	selectAppInfo 
     * @param appId
     * @return 业务平台信息
     * @Date	2015年4月22日
     * @Description:根据appID获得业务平台信息
     */
    public Result<App> selectAppInfo(int appId);
}
