package com.sogou.pay.service.connect.impl;

import com.sogou.pay.common.http.model.RequestModel;
import com.sogou.pay.common.http.utils.HttpConstant;
import com.sogou.pay.common.http.utils.HttpMethodEnum;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.service.connect.HttpClientService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * Created by hujunfei Date: 15-1-4 Time: 上午10:41
 */
@Service
public class HttpClientServiceImpl implements HttpClientService {
    @Autowired
    private HttpClient httpClient;

    @Override
    public String executeStr(RequestModel requestModel) {
        HttpEntity httpEntity = execute(requestModel);
        try {
            String charset = EntityUtils.getContentCharSet(httpEntity);
            if (StringUtil.isBlank(charset)) {
                charset = HttpConstant.DEFAULT_CHARSET;
            }
            String value = EntityUtils.toString(httpEntity, charset);
            if (!StringUtil.isBlank(value)) {
                value = value.trim();
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException("http request error ", e);
        }
    }

    public HttpEntity execute(RequestModel requestModel) {
        try {
            return executePrivate(requestModel);
        } catch (Exception e) {
            throw new RuntimeException("http request error ", e);
        }
    }

    private HttpEntity executePrivate(RequestModel requestModel) {
        if (requestModel == null) {
            throw new NullPointerException("requestModel 不能为空");
        }
        HttpRequestBase httpRequest = getRequest(requestModel);
        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            //302如何处理
            if (responseCode == HttpStatus.SC_OK) {
                return httpResponse.getEntity();
            }
            String params = EntityUtils.toString(requestModel.getRequestEntity(), HttpConstant.DEFAULT_CHARSET);
            throw new RuntimeException("http response error code: " + responseCode + " url:" + requestModel.getUrl() + " params:" + params);
        } catch (IOException e) {
            throw new RuntimeException("http request error ", e);
        }
    }

    private HttpRequestBase getRequest(RequestModel requestModel) {
        HttpRequestBase httpRequest = null;
        HttpMethodEnum method = requestModel.getHttpMethodEnum();
        switch (method) {
            case GET:
                httpRequest = new HttpGet(requestModel.getUrlWithParam());
                break;
            case POST:
                HttpPost httpPost = new HttpPost(requestModel.getUrl());
                httpPost.setEntity(requestModel.getRequestEntity());
                httpRequest = httpPost;
                break;
            case PUT:
                HttpPut httpPut = new HttpPut(requestModel.getUrl());
                httpPut.setEntity(requestModel.getRequestEntity());
                httpRequest = httpPut;
                break;
            case DELETE:
                httpRequest = new HttpDelete(requestModel.getUrl());
                break;
        }
        httpRequest.setHeaders(requestModel.getHeaders());
        return httpRequest;
    }
}
