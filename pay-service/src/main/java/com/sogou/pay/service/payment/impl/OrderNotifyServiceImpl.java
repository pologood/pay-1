package com.sogou.pay.service.payment.impl;

import com.sogou.pay.service.dao.OrderNotifyDao;
import com.sogou.pay.service.payment.OrderNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * User: Liwei
 * Date: 15/3/10
 * Time: 下午3:36
 * Description:
 */

@Service
public class OrderNotifyServiceImpl implements OrderNotifyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderNotifyServiceImpl.class);
    @Autowired
    private OrderNotifyDao orderNotifyDao;

    private static final int TOTAL_MAX_BLOCK_APPID = 50; //每个应用最多允许有多少定时任务
    private static final int AVG_MAX_NOTIFY_APPID = 3; //每个应用最多允许的平均通知次数

    public boolean isBlockedAppid(int appid){
        HashMap result = orderNotifyDao.listAppIdNum(appid);
        /*返回样例:
        {appid_count=3, notify_num=3, appid=1000}*/
        if(result != null) {
            Map<Integer,Integer> map1 = new HashMap<>();
            Map<Integer,Integer> map2 = new HashMap<>();

            map1.put(Integer.valueOf(result.get("appid").toString()),Integer.valueOf(result.get("appid_count").toString())); //每个应用有多少条在通知
            map2.put(Integer.valueOf(result.get("appid").toString()),Integer.valueOf(result.get("notify_num").toString()));  //每个应用平均通知次数
            //如果超过每个应用最多允许有多少定时任务或者超过每个应用最多允许的平均通知次数,则返回true
            if(map2.get(appid)/map1.get(appid)>AVG_MAX_NOTIFY_APPID || map1.get(appid)>TOTAL_MAX_BLOCK_APPID ) {
                LOGGER.info("OrderNotifyImpl isBlockedAppid appid={} " ,appid);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
