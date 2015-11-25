package com.sogou.pay.common.http.utils;

/**
 * Http协议相关常量类
 * User: shipengzhi
 * Date: 13-6-18
 * Time: 下午11:38
 */
public class HttpConstant {
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final class HttpMethod {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
    }

    public static final class HeaderType {
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
        public static final String AUTHORIZATION = "Authorization";
    }

    public static final class WWWAuthHeader {
        public static final String REALM = "realm";
    }

    public static final class ContentType {
        public static final String URL_ENCODED = "application/x-www-form-urlencoded";
        public static final String UPLOAD_FILE = "multipart/form-data";
        public static final String JSON = "application/json";
        public static final String HTML_TEXT = "text/html";
        public static final String XML_TEXT = "text/xml";
    }

}
