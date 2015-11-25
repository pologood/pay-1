package com.sogou.pay.common.http.model;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.common.http.utils.HttpConstant;
import com.sogou.pay.common.http.utils.HttpMethodEnum;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-2
 * Time: 下午10:24
 */
public class RequestModelJSON extends RequestModel {

    private static final Logger logger = LoggerFactory.getLogger(RequestModelJSON.class);

    /**
     * RequestModelJSON
     * 默认为POST请求
     *
     * @param url 要请求的参数
     */
    public RequestModelJSON(String url) {
        super(url);
        this.addHeader(HttpConstant.HeaderType.CONTENT_TYPE, HttpConstant.ContentType.JSON);
        this.setHttpMethodEnum(HttpMethodEnum.POST);
    }

    /**
     * 返回json格式的参数
     *
     * @return
     */
    @Override
    public HttpEntity getRequestEntity() {
        try {
            String json = JSON.toJSONString(params);
            StringEntity entity = new StringEntity(json);
            entity.setContentType(HttpConstant.ContentType.JSON);
            entity.setContentEncoding(DEFAULT_ENCODE);

            return entity;
        } catch (UnsupportedEncodingException e) {
            logger.error("http param url encode error ", e);
            throw new RuntimeException("http param url encode error", e);
        }
    }
}
