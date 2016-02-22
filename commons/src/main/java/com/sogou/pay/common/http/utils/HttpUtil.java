package com.sogou.pay.common.http.utils;


import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.common.utils.StringUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.util.Map;

/**
 * User: Liwei
 * Date: 15/3/13
 * Time: 下午4:31
 * Description:
 */
public final class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String packHttpGetUrl(String rUrl, Map params) {
        return packGetUrl(rUrl, params, new String[]{"https", "http"});
    }

    public static String packHttpsGetUrl(String rUrl, Map params) {
        return packGetUrl(rUrl, params, new String[]{"https"});
    }

    private static String packGetUrl(String rUrl, Map params, String[] protocols) {
        URL url;
        try {
            url = new URL(rUrl);
            boolean isLegalUrl = false;
            for (String proto : protocols) {
                if (proto.equals(url.getProtocol())) {
                    isLegalUrl = true;
                    break;
                }
            }
            if (ArrayUtils.isNotEmpty(protocols) && !isLegalUrl) {
                throw new IllegalArgumentException("Not https URL");
            }

            String newUrl = url.toString();
            if (url.getQuery() != null) {
                newUrl = newUrl.substring(0, newUrl.length() - url.getQuery().length() - 1);
            }

            if (MapUtil.isEmpty(params)) {
                return newUrl;
            } else {
                return newUrl + "?" + packUrlParams(params);
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL parse error");
        }
    }

    public static String packUrlParams(Map params) {
        if (MapUtil.isEmpty(params)) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            Object[] keys = params.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                String key = (String) keys[i];
                Object value = params.get(key);
                if (StringUtil.isBlank(key) || value == null) {
                    continue;
                }
                sb.append(URLEncoder.encode(key, HttpConstant.DEFAULT_CHARSET));
                sb.append("=");
                sb.append(URLEncoder.encode(value.toString(), HttpConstant.DEFAULT_CHARSET));
                sb.append("&");
            }
            if(sb.length()>0){
                sb.deleteCharAt(sb.length()-1);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("URL params error");
        }
    }


    public static ResultMap extractUrlParams(String params){
        ResultMap result = ResultMap.build();
        try {
            String []pairs = params.split("&");
            for(String pair: pairs){
                String []param = pair.split("=");
                result.addItem(param[0], param[1]);
            }
        }catch (Exception ex){
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }


    public static String getRequestInfo(HttpServletRequest request) {
        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        Map<String, String[]> params = request.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        // 去掉最后一个空格
        if (queryString.length() > 0) {
            queryString = queryString.substring(0, queryString.length() - 1);
        }

        return "receive callback:" + method + ":" + url + "?" + queryString;
    }

    public static boolean isReached(String testUrl) throws IOException {
        if (StringUtil.checkExistNullOrEmpty(testUrl)) {
            return false;
        }
        URL url = new URL(testUrl);
        URLConnection conn = url.openConnection();
        String str = conn.getHeaderField(0);
        if (str == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    @Profiled(el = true, logger = "httpClientTimingLogger", tag = "HttpUtil_sendPost",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;
            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true, 默认情况下是false;
            httpUrlConnection.setDoOutput(true);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpUrlConnection.setDoInput(true);
            // Post 请求不能使用缓存
            httpUrlConnection.setUseCaches(false);
            // 设置通用的请求属性
            httpUrlConnection.setRequestProperty("accept", "*/*");
            httpUrlConnection.setRequestProperty("connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 设定请求的方法为"POST"，默认是GET
            httpUrlConnection.setRequestMethod("POST");
            //设置连接超时时间
            httpUrlConnection.setConnectTimeout(30000);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(httpUrlConnection.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送 POST 请求出现异常！" + e.getMessage());
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
