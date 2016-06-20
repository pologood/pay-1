package com.sogou.pay.notify.listener;

import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.notify.enums.NotifyTypeEnum;
import com.sogou.pay.notify.service.NotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName QueuePayNotifyListener
 * @Date 2014年9月16日
 * @Description:支付结果通知队列监听
 */
@SuppressWarnings("unchecked")
@Service
public class QueuePayNotifyListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(QueuePayNotifyListener.class);

    @Autowired
    private NotifyService notifyService;

    /**
     * @param message
     * @Author qibaichao
     * @MethodName onMessage
     * @Date 2014年9月18日
     * @Description:
     */
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                final ObjectMessage objectMessage = (ObjectMessage) message;
                logger.info("queue pay notify listener begin : {}", JSONUtil.Bean2JSON(objectMessage.getObject()));
                Map map = (Map) objectMessage.getObject();
                String payId = String.valueOf(map.get("payId"));
                String notifyUrl = String.valueOf(map.remove("appBgUrl"));
                notifyService.firstNotify(NotifyTypeEnum.PAY_NOTIFY.value(), payId, notifyUrl, map);
                logger.info("queue pay notify listener end");
            }
        } catch (Exception e) {
            logger.error("queue pay notify listener error:", e);
        }
    }
}
