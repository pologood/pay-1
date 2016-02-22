package com.sogou.pay.common.http.model;

import com.sogou.pay.common.http.utils.HttpConstant;
import com.sogou.pay.common.http.utils.HttpMethodEnum;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.common.utils.XMLUtil;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * 用于发送xml时使用，默认采用post方式
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-2
 * Time: 下午8:31
 */
public class RequestModelXml extends RequestModel {

    private static final Logger logger = LoggerFactory.getLogger(RequestModelXml.class);

    //root节点的名称
    private String rootNodeName;

    public void setRootNodeName(String rootNodeName) {
        if (StringUtil.isBlank(rootNodeName)) {
            throw new IllegalArgumentException("root node may not be null");
        }
        this.rootNodeName = rootNodeName;
    }

    public String getRootNodeName() {
        return this.rootNodeName;
    }

    /**
     * 初始化RequestModelXml
     * 默认为POST请求
     *
     * @param url          要请求的参数
     * @param rootNodeName xml中Root节点的名称
     */
    public RequestModelXml(String url, String rootNodeName) {
        super(url);
        this.addHeader(HttpConstant.HeaderType.CONTENT_TYPE, HttpConstant.ContentType.XML_TEXT);
        this.setRootNodeName(rootNodeName);
        this.setHttpMethodEnum(HttpMethodEnum.POST);
    }

    /**
     * 返回xml格式的参数
     *
     * @return
     */
    @Override
    public HttpEntity getRequestEntity() {
        String xmlStr = XMLUtil.Map2XML(this.getRootNodeName(), this.params);
        try {
            return new StringEntity(xmlStr, HttpConstant.ContentType.XML_TEXT,
                    DEFAULT_ENCODE);
        } catch (UnsupportedEncodingException e) {
            logger.error("http param url encode error ", e);
            throw new RuntimeException("http param url encode error", e);
        }
    }
}
