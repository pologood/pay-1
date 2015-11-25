package com.sogou.pay.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记这个接口是供服务器端调用的, 使用app secret加密
 * User: Liwei
 * Date: 2015/1/15
 * Time: 14:31
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ServerApi {
}
