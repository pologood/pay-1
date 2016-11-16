
package com.sogou.pay.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.Serializable;
import java.util.Map;

/**
 * 商户后台通知Producer
 */
@Service
public class  QueueNotifyProducer{

    @Autowired(required=false)
    private JmsTemplate jmsPayTemplate;

    @Autowired(required=false)
    private JmsTemplate jmsRefundTemplate;

    public void sendPayMessage(final Map<String,String> map) {

        jmsPayTemplate.send(new MessageCreator() {
            private Message message;

            public Message createMessage(Session session) throws JMSException {
                message = session.createObjectMessage((Serializable) map);
                return message;
            }
        });
    }

    public void sendRefundMessage(final Map<String,String> map) {

        jmsRefundTemplate.send(new MessageCreator() {
            private Message message;

            public Message createMessage(Session session) throws JMSException {
                message = session.createObjectMessage((Serializable) map);
                return message;
            }
        });
    }
}
