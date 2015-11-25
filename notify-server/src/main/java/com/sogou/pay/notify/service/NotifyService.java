package com.sogou.pay.notify.service;

import com.sogou.pay.notify.entity.NotifyErrorLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by qibaichao on 2015/4/8.
 */
public interface NotifyService {


    /**
     * @param notifyErrorLog
     * @Author qibaichao
     * @MethodName insertErrorLog
     * @Date 2014年9月22日
     * @Description:
     */
    public void insertErrorLog(NotifyErrorLog notifyErrorLog);

    /**
     * @param notifyErrorLog
     * @Author qibaichao
     * @MethodName updateErrorLog
     * @Date 2014年9月22日
     * @Description:
     */
    public void updateErrorLog(NotifyErrorLog notifyErrorLog);

    /**
     * @param notifyType
     * @param status
     * @param currentTime
     * @return
     * @Author qibaichao
     * @MethodName queryByNotifyTypeStatus
     * @Date 2014年9月22日
     * @Description:
     */
    public List<NotifyErrorLog> queryByNotifyTypeStatus(int notifyType, int status, Date currentTime);

    /**
     * @Author qibaichao
     * @MethodName firstNotify
     * @Date 2014年9月18日
     * @Description:初次通知
     */
    public void firstNotify(int type,String outTradeNo, String notifyUrl, Map<String, String> notifyParam);

    /**
     * @Author qibaichao
     * @MethodName scheduledNotify
     * @Date 2014年9月18日
     * @Description: 定时补偿通知
     */
    public void scheduledNotify(NotifyErrorLog notifyErrorLog);

    /**
     * @param notifyErrorLog
     * @return
     * @Author qibaichao
     * @MethodName isNotifyable
     * @Date 2014年9月18日
     * @Description:判断通知次数
     */
    public boolean isNotifyable(NotifyErrorLog notifyErrorLog);

}
