package com.sogou.pay.thirdpay.biz.utils;


import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.*;
import com.sogou.pay.common.utils.XMLUtil;

import org.apache.commons.httpclient.NameValuePair;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/1/14 15:41
 */
public class WechatHttpClient {

    private static final String USER_AGENT_VALUE = "Mozilla/4.0 (compatible; MSIE 6.0; Windows XP)";

    private static final String JKS_CA_FILENAME = "tenpay_cacert.jks";

    private static final String JKS_CA_ALIAS = "tenpay";

    private static final String JKS_CA_PASSWORD = "";

    /**
     * ca证书文件
     */
    private File caFile;

    /**
     * 证书文件
     */
    private File certFile;

    /**
     * 证书密码
     */
    private String certPasswd;

    /**
     * 请求内容，无论post和get，都用get方式提供
     */
    private String reqContent;

    /**
     * 应答内容
     */
    private String resContent;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 错误信息
     */
    private String errInfo;

    /**
     * 超时时间,以秒为单位
     */
    private int timeOut;

    /**
     * http应答编码
     */
    private int responseCode;

    /**
     * 字符编码
     */
    private String charset = "UTF-8";

    private InputStream inputStream;

    /**
     * 设置证书信息
     *
     * @param certFile   证书文件
     * @param certPasswd 证书密码
     */
    public void setCertInfo(File certFile, String certPasswd) {
        this.certFile = certFile;
        this.certPasswd = certPasswd;
    }

    /**
     * 设置ca
     */
    public void setCaInfo(File caFile) {
        this.caFile = caFile;
    }

    /**
     * 设置请求内容
     *
     * @param reqContent 表求内容
     */
    public void setReqContent(String reqContent) {
        this.reqContent = reqContent;
    }

    /**
     * 获取结果内容
     *
     * @return String
     */
    public String getResContent() {
        try {
            this.doResponse();
        } catch (IOException e) {
            this.errInfo = e.getMessage();
            // return "";
        }

        return this.resContent;
    }

    /**
     * 设置请求方法post或者get
     *
     * @param method 请求方法post/get
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 获取错误信息
     *
     * @return String
     */
    public String getErrInfo() {
        return this.errInfo;
    }

    /**
     * 设置超时时间,以秒为单位
     *
     * @param timeOut 超时时间,以秒为单位
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * 获取http状态码
     *
     * @return int
     */
    public int getResponseCode() {
        return this.responseCode;
    }

    /**
     * 拼装请求参数到URL
     */
    @SuppressWarnings("rawtypes")
    public static String getRequestContextParam(Map<String, String> sPara) {
        List<String> keys = new ArrayList<String>(sPara.keySet());
        Collections.sort(keys);
        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = sPara.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + URLEncoder.encode(value);
            } else {
                prestr = prestr + key + "=" + URLEncoder.encode(value) + "&";
            }
        }
        return prestr;
    }

    public boolean call() {
        boolean isRet = false;
        // http
        if (null == this.caFile && null == this.certFile) {
            try {
                this.callHttp();
                isRet = true;
            } catch (IOException e) {
                this.errInfo = e.getMessage();
            }
            return isRet;
        }
        // https
        try {
            this.callHttps();
            isRet = true;
        } catch (UnrecoverableKeyException e) {
            this.errInfo = e.getMessage();
        } catch (KeyManagementException e) {
            this.errInfo = e.getMessage();
        } catch (CertificateException e) {
            this.errInfo = e.getMessage();
        } catch (KeyStoreException e) {
            this.errInfo = e.getMessage();
        } catch (NoSuchAlgorithmException e) {
            this.errInfo = e.getMessage();
        } catch (IOException e) {
            this.errInfo = e.getMessage();
        }

        return isRet;

    }

    protected void callHttp() throws IOException {

        if ("POST".equals(this.method.toUpperCase())) {
            String url = HttpClientUtil.getURL(this.reqContent);
            String queryString = HttpClientUtil.getQueryString(this.reqContent);
            byte[] postData = queryString.getBytes(this.charset);
            this.httpPostMethod(url, postData);

            return;
        }

        this.httpGetMethod(this.reqContent);

    }

    protected void callHttps() throws IOException, CertificateException,
                                      KeyStoreException, NoSuchAlgorithmException,
                                      UnrecoverableKeyException, KeyManagementException {

        // ca目录
        String caPath = this.caFile.getParent();
        File jksCAFile = new File(caPath + "/"
                                  + WechatHttpClient.JKS_CA_FILENAME);
        if (!jksCAFile.isFile()) {
            X509Certificate cert = (X509Certificate) HttpClientUtil
                .getCertificate(this.caFile);
            FileOutputStream out = new FileOutputStream(jksCAFile);
            // store jks file
            HttpClientUtil.storeCACert(cert, WechatHttpClient.JKS_CA_ALIAS,
                                       WechatHttpClient.JKS_CA_PASSWORD, out);
            out.close();
        }

        FileInputStream trustStream = new FileInputStream(jksCAFile);
        FileInputStream keyStream = new FileInputStream(this.certFile);
        SSLContext sslContext = HttpClientUtil.getSSLContext(trustStream,
                                                             WechatHttpClient.JKS_CA_PASSWORD,
                                                             keyStream, this.certPasswd);
        // 关闭流
        keyStream.close();
        trustStream.close();

        if ("POST".equals(this.method.toUpperCase())) {
            String url = HttpClientUtil.getURL(this.reqContent);
            String queryString = HttpClientUtil.getQueryString(this.reqContent);
            byte[] postData = queryString.getBytes(this.charset);

            this.httpsPostMethod(url, postData, sslContext);
            return;
        }
        this.httpsGetMethod(this.reqContent, sslContext);

    }

    public boolean callHttpPost(String url, String postdata) {
        boolean flag = false;
        byte[] postData;
        try {
            postData = postdata.getBytes(this.charset);
            this.httpPostMethod(url, postData);
            flag = true;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return flag;
    }

    /**
     * 以http post方式通信
     */
    protected void httpPostMethod(String url, byte[] postData)
        throws IOException {

        HttpURLConnection conn = HttpClientUtil.getHttpURLConnection(url);
        this.doPost(conn, postData);
    }

    /**
     * 以http get方式通信
     */
    protected void httpGetMethod(String url) throws IOException {

        HttpURLConnection httpConnection = HttpClientUtil
            .getHttpURLConnection(url);
        this.setHttpRequest(httpConnection);
        httpConnection.setRequestMethod("GET");
        this.responseCode = httpConnection.getResponseCode();
        this.inputStream = httpConnection.getInputStream();

    }

    /**
     * 以https get方式通信
     */
    protected void httpsGetMethod(String url, SSLContext sslContext)
        throws IOException {

        SSLSocketFactory sf = sslContext.getSocketFactory();

        HttpsURLConnection conn = HttpClientUtil.getHttpsURLConnection(url);

        conn.setSSLSocketFactory(sf);

        this.doGet(conn);

    }

    protected void httpsPostMethod(String url, byte[] postData,
                                   SSLContext sslContext) throws IOException {
        SSLSocketFactory sf = sslContext.getSocketFactory();
        HttpsURLConnection conn = HttpClientUtil.getHttpsURLConnection(url);
        conn.setSSLSocketFactory(sf);
        this.doPost(conn, postData);

    }

    /**
     * 设置http请求默认属性
     */
    protected void setHttpRequest(HttpURLConnection httpConnection) {

        // 设置连接超时时间
        httpConnection.setConnectTimeout(this.timeOut * 1000);
        // User-Agent
        httpConnection.setRequestProperty("User-Agent",
                                          WechatHttpClient.USER_AGENT_VALUE);
        // 不使用缓存
        httpConnection.setUseCaches(false);
        // 允许输入输出
        httpConnection.setDoInput(true);
        httpConnection.setDoOutput(true);
    }

    /**
     * 处理应答
     */
    protected void doResponse() throws IOException {
        if (null == this.inputStream) {
            return;
        }
        // 获取应答内容
        this.resContent = HttpClientUtil.InputStreamTOString(this.inputStream,
                                                             this.charset);
        // 关闭输入流
        this.inputStream.close();
    }

    /**
     * post方式处理
     */
    protected void doPost(HttpURLConnection conn, byte[] postData)
        throws IOException {
        // 以post方式通信
        conn.setRequestMethod("POST");
        // 设置请求默认属性
        this.setHttpRequest(conn);
        // Content-Type
        conn.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded");

        BufferedOutputStream out = new BufferedOutputStream(
            conn.getOutputStream());
        final int len = 1024; // 1KB
        HttpClientUtil.doOutput(out, postData, len);
        // 关闭流
        out.close();
        // 获取响应返回状态码
        this.responseCode = conn.getResponseCode();
        // 获取应答输入流
        this.inputStream = conn.getInputStream();
    }

    /**
     * get方式处理
     */
    protected void doGet(HttpURLConnection conn) throws IOException {

        // 以GET方式通信
        conn.setRequestMethod("GET");
        // 设置请求默认属性
        this.setHttpRequest(conn);
        // 获取响应返回状态码
        this.responseCode = conn.getResponseCode();
        // 获取应答输入流
        this.inputStream = conn.getInputStream();
    }

    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取支付宝的处理结果 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值
     * 如：buildRequest("",
     */
    @SuppressWarnings({"unchecked", "unused"})
    public static ResultMap buildRequest(String requestUrl,
                                         PMap sPara, String requestMethod,
                                         String charset,
                                         int timeOut, String caInfo, String certInfo,
                                         String certId) {
        ResultMap result = ResultMap.build();
        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        // 设置编码集
        request.setCharset(charset);
        request.setParameters(generatNameValuePair(sPara));
        request.setUrl(requestUrl);
        request.setMethod(requestMethod.toUpperCase());
        HttpResponse response = null;
        String rescontent = null;
        // 设置通信参数
        WechatHttpClient httpClient = new WechatHttpClient();
        // 设置请求返回的等待时间
        httpClient.setTimeOut(timeOut);
        // 设置ca证书
        httpClient.setCaInfo(new File(caInfo));
        // 设置个人(商户)证书
        httpClient.setCertInfo(new File(certInfo), certId);
        // 设置发送类型POST
        httpClient.setMethod("POST");
        requestUrl = requestUrl + "?" + XMLUtil.mapToXmlString("xml", sPara);
        // 设置请求内容
        httpClient.setReqContent(requestUrl);
        try {
            httpClient.callHttps();
            // 设置结果参数
            rescontent = httpClient.getResContent();
        } catch (UnrecoverableKeyException | KeyManagementException
            | CertificateException | KeyStoreException
            | NoSuchAlgorithmException | IOException e) {
            result.withError(ResultStatus.THIRD_REFUND_WECHAT_HTTP_ERROR);
            return result;
        }
        if (rescontent == null) {
            result.withError(ResultStatus.THIRD_REFUND_WECHAT_HTTP_ERROR);
            return result;
        }
        result.addItem("responseData", rescontent);
        return result;
    }

    /**
     * MAP类型数组转换成NameValuePair类型
     *
     * @param properties MAP类型数组
     * @return NameValuePair类型数组
     */
    private static NameValuePair[] generatNameValuePair(
        Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(),
                                                   entry.getValue());
        }

        return nameValuePair;
    }

}
