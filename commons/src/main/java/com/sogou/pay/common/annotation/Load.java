package com.sogou.pay.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author qibaichao
 * @ClassName Load
 * @Date 2015年03月11日
 * @Description:
 *               声明从指定的一个locator中注入依赖bean
 *               该注解用于类的成员变量上面，支持静态变量和私有变量
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Load {
    /**
     * 指定locator的class类
     * 
     * @return
     */
    Class<?> locator();

    /**
     * bean的名称，默认情况下根据class type加载，指定名字则按名字加载
     * 
     * @return
     */
    String name() default "";

}
