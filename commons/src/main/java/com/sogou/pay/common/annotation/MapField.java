package com.sogou.pay.common.annotation;

import java.lang.annotation.*;

/**
 * Created by hujunfei Date: 15-1-9 Time: 下午6:28
 * 转换Bean时对应Map的键
 * <br/>如：
 * <br/>Map为{"username": "jerry"}
 * <p>
 * class SomeBean {
 * <br/>&nbsp;&nbsp;...
 * <br/>&nbsp;&nbsp;@MapField(key = "username")
 * <br/>&nbsp;&nbsp;private String name;    // setter省略
 * <br/>&nbsp;&nbsp;...
 * <br/>}
 * </p>
 * <p/>则设置name为Map中key为username的值, 即"jerry"
 * <p>映射关系
 * <br/>[Map]&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[SomeBean]
 * <br/>username ----> name
 * <p/>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MapField {
    String key();
//    boolean forceMapping() default false;
}
