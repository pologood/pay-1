package com.sogou.pay.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记这个接口是供客户端(sdk)调用的, 使用app key加密
 * User: Liwei
 * Date: 2015/1/15
 * Time: 14:28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ClientApi {


}