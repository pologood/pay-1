
package com.sogou.pay;

import com.sogou.pay.common.spring.DefaultServiceLocator;
import org.springframework.context.ApplicationContext;


/**
 * @Author qibaichao
 * @ClassName PayPlatformBizServiceLocator
 * @Date 2015年03月11日
 * @Description:
 */
public class PayPlatformBizServiceLocator extends DefaultServiceLocator {

    /**
     * The context.
     */
    private static ApplicationContext context;

    /**
     * Gets the context.
     *
     * @return the context
     */
    public static ApplicationContext getContext() {
        return context;
    }

    static {
        try {
            context = getApplicationContext(PayPlatformBizServiceLocator.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
