package com.sogou.pay.notify.server;

import com.sogou.pay.common.spring.DefaultServiceLocator;
import com.sogou.pay.notify.config.NotifyTime;
import org.springframework.context.ApplicationContext;

/**
 * @Author qibaichao
 * @ClassName PayNotifyLocator
 * @Date 2015年03月11日
 * @Description:
 */
public class NotifyServerLocator extends DefaultServiceLocator {
    /**
     * The context.
     */
    private static ApplicationContext context;

    static {
        try {
            context = getApplicationContext(NotifyServerLocator.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the application context.
     *
     * @return the application context
     */
    public static ApplicationContext getApplicationContext() {
        if (context == null) {
            throw new RuntimeException("Spring loading error!");
        }
        return context;
    }

    public static NotifyTime getNotifyTime() {
        return (NotifyTime) getApplicationContext().getBean(NotifyTime.class);
    }
}