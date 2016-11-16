package com.sogou.pay.common.constraint;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by hjf on 15-3-2.
 */
public class AmountValidator implements ConstraintValidator<Amount, String> {

    @Override
    public void initialize(Amount constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 正数，不超过10位
        String regPosNumber = "^(([1-9][0-9]{0,9})|([0]{1}))((\\.[0-9]{2})|())$";
        return StringUtils.isEmpty(value) || value.matches(regPosNumber);
    }
}