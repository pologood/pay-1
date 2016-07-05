package com.sogou.pay.notify.server;

import com.sogou.pay.common.spring.DefaultServiceLocator;
import org.springframework.context.ApplicationContext;

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
     */
    public static ApplicationContext getApplicationContext() {
        if (context == null) {
            throw new RuntimeException("Spring loading error!");
        }
        return context;
    }
}