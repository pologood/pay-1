/*
 * Copyright 2014-2016 cyou.cn All right reserved. This software is the
 * confidential and proprietary information of cyou.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cyou.cn.
 */
package com.sogou.pay.service.connect;

import com.sogou.pay.common.Model.AppRefundNotifyModel;
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
 * @Author qibaichao
 * @ClassName QueuePayNotifyProducer
 * @Date 2014年9月17日
 * @Description:商户后台通知Producer
 */
@Service
public class  QueueNotifyProducer{

    @Autowired(required=false)
    private JmsTemplate jmsPayTemplate;

    @Autowired(required=false)
    private JmsTemplate jmsRefundTemplate;

    /**
     * @param map
     * @Author qibaichao
     * @MethodName sendMessage
     * @Date 2014年9月17日
     * @Description:发送消息
     */
    public void sendPayMessage(final Map<String,String> map) {

        jmsPayTemplate.send(new MessageCreator() {
            private Message message;

            public Message createMessage(Session session) throws JMSException {
                message = session.createObjectMessage((Serializable) map);
                return message;
            }
        });
    }

    /**
     * @param appRefundNotifyModel
     * @Author qibaichao
     * @MethodName sendMessage
     * @Date 2014年9月17日
     * @Description:发送消息
     */
    public void sendRefundMessage(final AppRefundNotifyModel appRefundNotifyModel) {

        jmsRefundTemplate.send(new MessageCreator() {
            private Message message;

            public Message createMessage(Session session) throws JMSException {
                message = session.createObjectMessage((Serializable) appRefundNotifyModel);
                return message;
            }
        });
    }
}
