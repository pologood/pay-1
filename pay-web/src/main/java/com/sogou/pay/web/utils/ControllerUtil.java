package com.sogou.pay.web.utils;

import com.sogou.pay.common.types.PMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * Created by hujunfei Date: 15-1-5 Time: 下午3:15
 */
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

    /**
     * 检查Ru的安全性，TODO: 将安全相关都放在一起
     *
     * @param ru
     * @return
     */
    public static boolean checkRu(String ru) {
        return true;
    }

    public static PMap<String, String> getParamPMap(HttpServletRequest request) {
        PMap<String, String> params = new PMap<>();
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            params.put(key, request.getParameter(key));
        }
        return params;
    }

    public static PMap<String, String> getXmlParamPMap(HttpServletRequest request) throws IOException, DocumentException {
        PMap<String, String> params = new PMap<>();
        InputStream inputStream = request.getInputStream();
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();
        List<Element> elementList = root.elements();

        for(Element e : elementList)
            params.put(e.getName(),e.getText());

        inputStream.close();
        inputStream = null;

        return params;
    }
}
