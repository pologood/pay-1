package com.sogou.pay.common.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;


/**
 * Created by hgq on 15-4-15.
 */
public class BankCardTypeValidator implements ConstraintValidator<BankCardType, String> {

    @Override
    public void initialize(BankCardType constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //银行卡类型只能是空格1,2,3
        return StringUtils.isEmpty(value) || "1".equals(value) || "2".equals(value) || "3".equals(value);
    }
}