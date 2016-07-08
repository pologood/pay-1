package com.sogou.pay.notify.listener;

import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.notify.enums.NotifyType;
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
 * 支付结果通知监听
 */
@Service
public class PayNotifyListener implements MessageListener {

  private static final Logger logger = LoggerFactory.getLogger(PayNotifyListener.class);

  @Autowired
  private NotifyService notifyService;

  @Override
  public void onMessage(Message message) {
    if (message instanceof ObjectMessage) {
      final ObjectMessage objectMessage = (ObjectMessage) message;
      try {
        Map notifyParam = (Map) objectMessage.getObject();
        logger.info("[onMessage] begin, {}", notifyParam);
        String payId = String.valueOf(notifyParam.get("payId"));
        String appBgURL = String.valueOf(notifyParam.remove("appBgUrl"));
        notifyService.firstNotify(NotifyType.PAY_NOTIFY, payId, appBgURL, notifyParam);

        logger.info("[onMessage] finish, {}", notifyParam);
      } catch (Exception e) {
        logger.error("[onMessage] failed, {}", e);
      }
    }
  }
}
