package com.sogou.pay.common.constraint;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Created by hgq on 15-4-15.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = BankCardTypeValidator.class)
@Documented
public @interface BankCardType {

    String message() default "银行卡类型取值范围是NULL、1、2、3";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
