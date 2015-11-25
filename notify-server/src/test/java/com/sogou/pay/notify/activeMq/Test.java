package com.sogou.pay.notify.activeMq;

import javax.jms.JMSException;

/**
 * 说明：
 *
 * @author xajava
 * @version 创建时间：2012-10-22 下午4:33:17
 */
public class Test {

    public static void main(String[] args) throws JMSException, Exception {
        JmsSender sender = new JmsSender();
        JmsReceiver receiver = new JmsReceiver();

        sender.sendMessage("bytes");
        sender.close();

//        receiver.receiveMessage();
//        receiver.close();

    }
}


