package com.sogou.pay.common.constraint;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by hujunfei Date: 15-1-16 Time: 下午2:49
 */
public class PositiveNumberValidator implements ConstraintValidator<PositiveNumber, String> {

    @Override
    public void initialize(PositiveNumber constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 正数，不超过11位
        String regPosNumber = "^[1-9][0-9]{0,10}$";
        return StringUtils.isEmpty(value) || value.matches(regPosNumber);
    }
}
