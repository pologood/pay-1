package com.sogou.pay.common.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by hujunfei Date: 15-1-16 Time: 下午2:38
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PositiveNumberValidator.class)
@Documented
public @interface PositiveNumber {

    String message() default "不符合正整数格式";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
