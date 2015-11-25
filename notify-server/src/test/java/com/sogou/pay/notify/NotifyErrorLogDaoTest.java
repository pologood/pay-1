package com.sogou.pay.notify;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.notify.dao.NotifyErrorLogDao;
import com.sogou.pay.notify.entity.NotifyErrorLog;
import com.sogou.pay.notify.utils.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qibaichao on 2015/4/9.
 */
public class NotifyErrorLogDaoTest extends BaseTest {

    @Autowired
    private NotifyErrorLogDao notifyErrorLogDao;


    @Test
    public void insert() {
        NotifyErrorLog notifyErrorLog = new NotifyErrorLog();
        notifyErrorLog.setErrorInfo("test");
        notifyErrorLog.setOuterId("test");
        notifyErrorLog.setNotifyType(1);
        notifyErrorLog.setNotifyUrl("http://zhongyi.sogou.com/tcm_offline/?op=mszy_pay_feedback");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("signType", "0");
        paramMap.put("orderMoney", "0.01");
        paramMap.put("orderId", "123456789004");
        paramMap.put("isSuccess", "T");
        paramMap.put("sign", "DB54A935239A21CE613E047CEA553E3D");
        paramMap.put("payId", "ZFD20151030155510140001");
        paramMap.put("tradeStatus", "TRADE_FINISHED");
        paramMap.put("successTime", "20151030153655");
        paramMap.put("appId", "5000");
        notifyErrorLog.setNotifyParams(JSON.toJSONString(paramMap));
        notifyErrorLog.setNextTime(DateUtils.nextTime());
        notifyErrorLogDao.insertErrorLog(notifyErrorLog);
    }

    @Test
    public void update() {

        NotifyErrorLog notifyErrorLog=notifyErrorLogDao.queryById(21L);
        // 通知失败,增加支付通知队列
        notifyErrorLog.setErrorInfo("error");
        // 下次通知时间
        notifyErrorLog.setNextTime(DateUtils.nextTime(new Date(), notifyErrorLog.getNotifyNum()));
        // 设置通知次数
        notifyErrorLog.setNotifyNum(notifyErrorLog.getNotifyNum() + 1);
        notifyErrorLogDao.updateErrorLog(notifyErrorLog);
    }


    @Test
    public void delete() {
        Long id = 9L;
        notifyErrorLogDao.deleteErrorLog(9);
    }
}
