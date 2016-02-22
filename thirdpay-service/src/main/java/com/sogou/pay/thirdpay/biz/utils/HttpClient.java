package com.sogou.pay.thirdpay.biz.utils;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;

import org.apache.commons.httpclient.NameValuePair;

import java.util.Map;

public class HttpClient {

    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取支付宝的处理结果 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值
     * 如：buildRequest("", "",sParaTemp)
     *
     * @param requestMethod 请求方式 GET/POST
     * @return 支付宝处理结果
     * @paramALIPAY_GATEWAY_NEW 支付宝网关地址
     * @strParaFileName 文件类型的参数名
     * @strFilePath 文件路径
     * @sParaTemp 请求参数数组
     */
    @SuppressWarnings({"unchecked", "unused"})
    public static ResultMap buildRequest(String requestUrl, PMap<String, String> sPara,
                                         String requestMethod, String charset) {
        ResultMap result = ResultMap.build();
        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        //设置编码集
        request.setCharset(charset);
        request.setParameters(generatNameValuePair(sPara));
        request.setUrl(requestUrl);
        request.setMethod(requestMethod.toUpperCase());
        request.setConnectionTimeout(8000);//设置超时时间为8s
        HttpResponse response;
        String strResult = null;
        try {
            response = httpProtocolHandler.execute(request, "", "");
            strResult = response.getStringResult();
        } catch (Exception e) {
            result.withError(ResultStatus.THIRD_QUERY_HTTP_ERROR);
            return result;
        }
        if (response == null) {
            result.withError(ResultStatus.THIRD_QUERY_HTTP_ERROR);
            return result;
        }

        result.addItem("responseData", strResult);
        return result;
    }

    /**
     * MAP类型数组转换成NameValuePair类型
     *
     * @param properties MAP类型数组
     * @return NameValuePair类型数组
     */
    private static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        return nameValuePair;
    }

}
