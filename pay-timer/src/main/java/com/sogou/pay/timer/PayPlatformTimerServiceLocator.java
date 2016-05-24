package com.sogou.pay.timer;

import com.sogou.pay.common.spring.DefaultServiceLocator;
import org.springframework.context.ApplicationContext;

/**
 * @Author qibaichao
 * @ClassName PayPlatformTimerServiceLocator
 * @Date 2015年03月11日
 * @Description:
 */
public class PayPlatformTimerServiceLocator extends DefaultServiceLocator {
    /** The context. */
    private static ApplicationContext context;

    static {
        try {
            context = getApplicationContext(PayPlatformTimerServiceLocator.class);
        }
        catch (Exception e) {
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
}