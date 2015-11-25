package com.sogou.pay.web.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.SortedSet;

/**
 * User: Liwei
 * Date: 2015/1/15
 * Time: 15:44
 */
public class ApiSignUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiSignUtil.class);
    public static final String SIGN_PARAM = "sign";

    public static String prepareSignRawString(HttpServletRequest request, String key) throws UnsupportedEncodingException {
        Enumeration names = request.getParameterNames();
        SortedSet<String> allParams = Sets.newTreeSet();

        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            if (name.equals(SIGN_PARAM)) {
                continue;
            }
            allParams.add(name + "=" + encodeValue(request, name));
        }
        if (allParams.size() <= 1) {
            LOGGER.error("Request params empty");
            throw new IllegalArgumentException("Request params empty");
        }
        return Joiner.on("&").join(allParams) + "&" + key;
    }

    private static String encodeValue(HttpServletRequest request, String name) throws UnsupportedEncodingException {
        return URLEncoder.encode(request.getParameter(name), "utf-8");
    }

}
