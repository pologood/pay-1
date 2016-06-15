package com.sogou.pay.web.utils;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ControllerUtil {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 检查参数的完整性和合法性
     *
     * @param params
     * @param <T>
     * @return
     */
    public static <T> List<String> validateParams(T params) {
        StringBuilder sb = new StringBuilder();

        List<String> infos = new ArrayList<>();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(params);
        for (ConstraintViolation<T> constraintViolation : constraintViolations) {
            infos.add(constraintViolation.getMessage());
            // sb.append(constraintViolation.getMessage()).append("|");
        }
        return infos;
        /*String validateResult;
        if (sb.length() > 0) {
            validateResult = sb.deleteCharAt(sb.length() - 1).toString();
        } else {
            validateResult = "";
        }
        return validateResult;*/
    }
}
