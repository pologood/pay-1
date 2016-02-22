package com.sogou.pay.common.http.model;

import com.google.common.base.Strings;

import com.sogou.pay.common.http.utils.HttpMethodEnum;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.CommonConstant;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.common.utils.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用于请求
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-28
 * Time: 下午2:06
 */
public class RequestModel {

    protected static final String DEFAULT_ENCODE = CommonConstant.DEFAULT_CHARSET;

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private String HEADER_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded;charset=utf-8";

    private static final Logger logger = LoggerFactory.getLogger(RequestModel.class);

    //要请求的地址
    private String url;

    //请求的方法，默认采用post请求
    private HttpMethodEnum httpMethodEnum;

    //提交的参数
    protected Map<String, Object> params;

    //提交的头信息
    private Map<String, String> headers;

    // 字符集
    private String charset;

    /**
     * 请求参数Model
     *
     * @param url 请求地址
     */
    public RequestModel(String url) {
        if (Strings.isNullOrEmpty(url)) {
            throw new IllegalArgumentException("url不能为空！");
        }
        this.url = url.trim();
        this.httpMethodEnum = HttpMethodEnum.GET;
        this.params = new HashMap<>();
        this.headers = new HashMap<>(1);
        this.headers.put(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);
    }

    /**
     * 请求参数Model
     *
     * @param url 请求地址
     */
    public RequestModel(String url, String method) {
        if (Strings.isNullOrEmpty(url)) {
            throw new IllegalArgumentException("url不能为空！");
        }
        this.url = url.trim();
        method = StringUtils.lowerCase(method);
        if (StringUtils.equals(method, "get")) {
            this.httpMethodEnum = HttpMethodEnum.GET;
        } else {
            this.httpMethodEnum = HttpMethodEnum.POST;
        }
        this.params = new HashMap<>();
        this.headers = new HashMap<>(1);
        this.headers.put(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public HttpMethodEnum getHttpMethodEnum() {
        return httpMethodEnum;
    }

    public void setHttpMethodEnum(HttpMethodEnum httpMethodEnum) {
        this.httpMethodEnum = httpMethodEnum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    /**
     * 检查是否存在参数
     *
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        if (StringUtil.isBlank(key)) {
            throw new IllegalArgumentException("key 不能为空");
        }
        return this.params.containsKey(key);
    }

    /**
     * 增加参数，如果原key已经存在一个value，则覆盖老参数
     *
     * @param key   参数名
     * @param value 参数值
     */
    public void addParam(String key, Object value) {
        if (StringUtil.isBlank(key)) {
            throw new IllegalArgumentException("key 不能为空");
        }
//        if (value == null || StringUtil.isBlank(value.toString())) {
//            throw new IllegalArgumentException("value 不能为空");
//        }
        this.params.put(key, value);
    }

    /**
     * @param key
     * @return
     */
    public Object getParam(String key) {
        return this.params.get(key);
    }

    /**
     * 删除参数
     *
     * @param key
     */
    public void deleteParams(String key) {
        if (StringUtil.isBlank(key)) {
            throw new IllegalArgumentException("key 不能为空");
        }
        params.remove(key);
    }

    /**
     * 把整个对象转换为map参数
     *
     * @param object
     */
    public void addParams(Object object) {
        Map<String, Object> param = BeanUtil.Bean2Map(object);
        if (param == null || param.isEmpty()) {
            return;
        }
        this.params.putAll(param);
    }

    public Header[] getHeaders() {
        if (headers.isEmpty()) {
            return new Header[0];
        }
        Header[] header = new Header[this.headers.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            header[i] = new BasicHeader(entry.getKey(), entry.getValue());
            i++;
        }
        return header;
    }

    public HttpEntity getRequestEntity() {
        List<NameValuePair> nameValuePairs = new ArrayList<>(this.params.size());
        for (Map.Entry<String, Object> entry : this.params.entrySet()) {
            NameValuePair param = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
            nameValuePairs.add(param);
        }
        try {
            return new UrlEncodedFormEntity(nameValuePairs, StringUtil.isBlank(charset) ? DEFAULT_ENCODE : charset);
        } catch (UnsupportedEncodingException e) {
            logger.error("http param url encode error ", e);
            throw new RuntimeException("http param url encode error", e);
        }
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getUrlWithParam() {
        if (MapUtil.isEmpty(params)) {
            return getUrl();
        }
        // String charset = CommonConstant.DEFAULT_CHARSET;
        String cset = StringUtil.isBlank(charset) ? DEFAULT_ENCODE : charset;
        String localUrl = getUrl();

        StringBuilder url = new StringBuilder(localUrl);
        url.append("?");
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (!StringUtil.isBlank(entry.getKey()) && entry.getValue() != null) {
                    url.append(URLEncoder.encode(entry.getKey(), cset));
                    url.append("=");
                    url.append(URLEncoder.encode(entry.getValue().toString(), cset));
                    url.append("&");
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("getUrlWithParam UnsupportedEncodingException", e);
            throw new RuntimeException("getUrlWithParam UnsupportedEncodingException ");
        }
        return url.toString();
    }

    public void setHEADER_CONTENT_TYPE_VALUE(String HEADER_CONTENT_TYPE_VALUE) {
        this.HEADER_CONTENT_TYPE_VALUE = HEADER_CONTENT_TYPE_VALUE;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
