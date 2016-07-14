package com.sogou.pay.common.constraint;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by hgq on 15-4-15.
 */
public class DateValidator implements ConstraintValidator<Date, String> {

    @Override
    public void initialize(Date constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 时间正则表达式(包括闰年)
        String regPosNumber = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229))(([01][0-9]|2[0-3])([0-5][0-9])([0-5][0-9]))$";
        return StringUtils.isEmpty(value) || value.matches(regPosNumber);
    }
}