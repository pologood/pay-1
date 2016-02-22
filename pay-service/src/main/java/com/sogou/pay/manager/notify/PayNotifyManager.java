package com.sogou.pay.manager.notify;

import java.util.Map;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.model.notify.PayNotifyModel;
import com.sogou.pay.service.entity.PayOrderInfo;

/**
 * @Author qibaichao
 * @ClassName PayNotifyManagerImpl
 * @Date 2015年04月09日
 * @Description:
 */
public interface PayNotifyManager {

    public ResultMap doProcess(PayNotifyModel payNotifyModel);

    public ResultMap<PMap<String,String>> getQueryOrderParam(Map map);
    
    public ResultMap<Map> getNotifyMap(PayOrderInfo payOrderInfo);
}
