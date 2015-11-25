package com.sogou.pay.notify.activeMq;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 说明： activemq send message
 *
 * @author xajava
 * @version 创建时间：2012-10-24 下午1:22:40
 */
public class JmsSender {

    private String USER = ActiveMQConnection.DEFAULT_USER;
    private String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    private String URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private String SUBJECT = "ActiveMQ.Demo";

    private Destination destination = null;
    private Connection conn = null;
    private Session session = null;
    private MessageProducer producer = null;

    public static void main(String[] args) throws JMSException, Exception {
        JmsSender sender = new JmsSender();
        JmsReceiver receiver = new JmsReceiver();

        sender.sendMessage("bytessss");
        sender.close();

    }

    // 初始化
    private void initialize() throws JMSException, Exception {
        // 连接工厂
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USER, PASSWORD, URL);
        conn = connectionFactory.createConnection();
        // 事务性会话，自动确认消息
        session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 消息的目的地（Queue/Topic）
        destination = session.createQueue(SUBJECT);
        // destination = session.createTopic(SUBJECT);
        // 消息的提供者（生产者）
        producer = session.createProducer(destination);
        // 不持久化消息
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    public void sendMessage(String msgType) throws JMSException, Exception {
        initialize();
        // 连接到JMS提供者（服务器）
        conn.start();
        // 发送文本消息
        if ("text".equals(msgType)) {
            String textMsg = "ActiveMQ Text Message!";
            TextMessage msg = session.createTextMessage();
            // TextMessage msg = session.createTextMessage(textMsg);
            msg.setText(textMsg);
            producer.send(msg);
        }
        // 发送Map消息
        if ("map".equals(msgType)) {
            MapMessage msg = session.createMapMessage();
            msg.setBoolean("boolean", true);
            msg.setShort("short", (short) 0);
            msg.setLong("long", 123456);
            msg.setString("MapMessage", "ActiveMQ Map Message!");
            producer.send(msg);
        }
        // 发送流消息
        if ("stream".equals(msgType)) {
            String streamValue = "ActiveMQ stream Message!";
            StreamMessage msg = session.createStreamMessage();
            msg.writeString(streamValue);
            msg.writeBoolean(false);
            msg.writeLong(1234567890);
            producer.send(msg);
        }
        // 发送对象消息
        if ("object".equals(msgType)) {
            JmsObjectMessageBean jmsObject = new JmsObjectMessageBean("ActiveMQ Object Message", 18, false);
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(jmsObject);
            producer.send(msg);
        }
        // 发送字节消息
        if ("bytes".equals(msgType)) {
            String byteValue = "字节消息";
            BytesMessage msg = session.createBytesMessage();
            msg.writeBytes(byteValue.getBytes());
            producer.send(msg);
        }
    }

    // 关闭连接
    public void close() throws JMSException {
        if (producer != null)
            producer.close();
        if (session != null)
            session.close();
        if (conn != null)
            conn.close();
    }

}