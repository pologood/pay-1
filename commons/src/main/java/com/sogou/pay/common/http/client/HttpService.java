package com.sogou.pay.common.http.client;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultBean;
import com.sogou.pay.common.types.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName HttpService
 * @Date 2014年9月12日
 * @Description: HTTP工具类
 */
public class HttpService {

    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);

    private static HttpService httpService = new HttpService();

    public static HttpService getInstance() {
        return httpService;
    }

    public Result doGet(String url, Map<String, Object> paramMap, String charset, SSLContext sslContext) {
        return doCall(url, null, paramMap, Request.GET, charset, sslContext);
    }

    public Result doGet(String url, String requestBody, String charset, SSLContext sslContext) {
        return doCall(url, requestBody, null, Request.GET, charset, sslContext);
    }

    public Result doPost(String url, Map<String, Object> paramMap, String charset, SSLContext sslContext) {
        return doCall(url, null, paramMap, Request.POST, charset, sslContext);
    }

    public Result doPost(String url, String requestBody, String charset, SSLContext sslContext) {
        return doCall(url, requestBody, null, Request.POST, charset, sslContext);
    }

    private Result doCall(String url, String requestBody, Map<String, Object> paramMap, int method, String charset, SSLContext sslContext) {

        logger.info(String.format("[doCall] 参数: url=%s, requestBody=%s, paramMap=%s, method=%s, charset=%s", url,
                requestBody, JSON.toJSONString(paramMap), method, charset));

        Result result = ResultBean.build();
        if (url == null || "".equals(url)) {
            logger.error("[doCall] 参数错误");
            result.withError(ResultStatus.SYSTEM_ERROR);
            return result;
        }
        try {
            // 构造request对象
            Request request = new Request();
            request.setUrl(url);
            request.setMethod(method);
            request.setSslContext(sslContext);
            if (charset != null)
                request.setCharset(charset);
            if (requestBody != null)
                request.setRequestBody(requestBody);
            else if (paramMap != null)
                request.addParam(paramMap);
            Response response = HttpCaller.getInstance().call(request);
            result.withReturn(new String(response.getByteData(), response.getCharset()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[doCall] HTTP请求失败, " + e.getMessage());
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }
}
