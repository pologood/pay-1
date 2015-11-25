package com.sogou.pay.manager.payment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.manager.payment.AppManager;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.payment.AppService;

@Component
public class AppManagerImpl implements AppManager {

    @Autowired
    private AppService appService;
    
    @Override
    public Result<App> selectAppInfo(int appId) {
        Result<App> result = ResultMap.build();
        App appInfo = appService.selectApp(appId);
        if(null == appInfo){
            result.withError(ResultStatus.PAY_APP_NOT_EXIST);
            return result;
        }
        return result.withReturn(appInfo);
    }
}
