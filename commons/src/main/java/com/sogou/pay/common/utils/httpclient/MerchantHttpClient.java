package com.sogou.pay.common.utils.httpclient;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.common.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName MerchantHttpClient
 * @Date 2014年9月12日
 * @Description: HTTP调用工具类
 */
public class MerchantHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(MerchantHttpClient.class);

    private String charset = "UTF-8";

    private MerchantHttpClient() {
    }

    private static MerchantHttpClient merchantHttpClient = null;

    public static MerchantHttpClient getInstance() {
        if (merchantHttpClient == null) {
            merchantHttpClient = new MerchantHttpClient();
        }
        return merchantHttpClient;
    }

    public MerchantResponse doGet(String path, Map<String, String> paramMap) {
        return doCall(path, paramMap, HttpMethod.GET);
    }

    public MerchantResponse doPost(String path, Map<String, String> paramMap) {
        return doCall(path, paramMap, HttpMethod.POST);
    }

    private MerchantResponse doCall(String path, Map<String, String> paramMap, HttpMethod method) {

        if (path == null || "".equals(path)) {
            throw new IllegalArgumentException("Parameter 'path' is empty.");
        }
        if (paramMap == null || paramMap.size() == 0) {
            throw new IllegalArgumentException("Parameter 'paramMap' is null.");
        }
        logger.info(String.format("http call params: method=%s|path=%s|paramMap=%s", method, path,
                JSON.toJSONString(paramMap)));

        MerchantResponse merchantResponse = new MerchantResponse();
        try {
            // 构造request对象
            Request request = new Request();
            request.setGateway(path);
            request.setMethod(method);
            request.setCharset(charset);
            request.addParam(paramMap);
            Response response = HttpCaller.getInstance().call(request);
            response.setRespCharset(charset);
            merchantResponse.setData(response.getByteResult());
            if (StringUtils.endsWithIgnoreCase(
                    StringUtils.trim(merchantResponse.getStringResult()), "success")) {
                merchantResponse.setSuccess(true);
            }
        } catch (Exception e) {
            merchantResponse.setMessage("http call failed. " + e.getMessage());
            logger.error(e.getMessage());
        }

        return merchantResponse;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

}
