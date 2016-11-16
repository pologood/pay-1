package com.sogou.pay.notify;

import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.notify.dao.NotifyToDoDao;
import com.sogou.pay.notify.entity.NotifyToDo;
import com.sogou.pay.notify.enums.NotifyType;
import com.sogou.pay.notify.service.NotifyService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class NotifyToDoTest extends BaseTest {

    @Autowired
    private NotifyToDoDao notifyToDoDao;

    @Autowired
    private NotifyService notifyService;

    @Test
    public void insert() {
        NotifyToDo notifyToDo = new NotifyToDo();
        notifyToDo.setErrorInfo("test");
        notifyToDo.setPayId("test");
        notifyToDo.setNotifyType(NotifyType.PAY_NOTIFY.value());
        notifyToDo.setNotifyUrl("http://center.pay.sogou.com/notify/testBgUrl");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("isSuccess", "T");
        paramMap.put("signType", "0");
        paramMap.put("appId", "1999");
        paramMap.put("orderId", "test");
        paramMap.put("payId", "test");
        paramMap.put("orderMoney", "0.01");
        paramMap.put("payStatus", "SUCCESS");
        paramMap.put("successTime", DateUtil.formatShortTime(new Date()));
        paramMap.put("sign", "DB54A935239A21CE613E047CEA553E3D");
        notifyToDo.setNotifyParams(JSONUtil.Bean2JSON(paramMap));
        notifyToDo.setNextTime(notifyService.nextNotifyTime(1));
        notifyToDoDao.insertNotifyToDo(notifyToDo);
    }

    @Test
    public void update() {
        NotifyToDo notifyToDo =notifyToDoDao.queryById(21L);
        if (Objects.nonNull(notifyToDo)) {
            // 通知失败,增加支付通知队列
            notifyToDo.setErrorInfo("error");
            // 设置通知次数
            notifyToDo.setNotifyNum(notifyToDo.getNotifyNum() + 1);
            // 下次通知时间
            notifyToDo.setNextTime(notifyService.nextNotifyTime(notifyToDo.getNotifyNum()));
            notifyToDoDao.updateNotifyToDo(notifyToDo);
        }
    }

    @Test
    public void delete() {
        Long id = 9L;
        notifyToDoDao.deleteNotifyToDo(9);
    }
}
