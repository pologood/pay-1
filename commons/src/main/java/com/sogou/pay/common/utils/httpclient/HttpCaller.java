package com.sogou.pay.common.utils.httpclient;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.CodingErrorAction;
import java.util.concurrent.TimeUnit;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author qibaichao
 * @ClassName HttpCaller
 * @Date 2014年9月12日
 * @Description:HTTP调用工具类，内部使用连接池管理
 */
public class HttpCaller {

    private static final Logger logger = LoggerFactory.getLogger(HttpCaller.class);



    /** 连接超时时间，由bean factory设置，缺省为8秒钟 */
    private static final int DEFAULT_CONNECTIONT_IMEOUT = 8000;

    /** 回应超时时间, 由bean factory设置，缺省为30秒钟 */
    private static final int DEFAULT_SO_TIMEOUT = 30000;

    /** 闲置连接超时时间, 由bean factory设置，缺省为30秒钟 */
    private static final int DEFAULT_IDLE_TIMEOUT = 30000;

    /**
     * 每个host默认最大连接数
     */
    private static final int DEFAULT_MAX_CONN_PERHOST = 30;

    /**
     * 默认全局最大连接数
     */
    private static final int DEFAULT_MAX_TOTAL_CONN = 90;

    /**
     * HTTP连接管理器，该连接管理器必须是线程安全的.
     */
    private PoolingHttpClientConnectionManager connectionManager;

    private static HttpCaller httpCaller = new HttpCaller();

    private IdleConnectionMonitorThread idleEvictThread;

    /**
     * 工厂方法
     * 
     * @return
     */
    public static HttpCaller getInstance() {
        return httpCaller;
    }

    /**
     * 私有的构造方法
     */
    private HttpCaller() {

        // 创建一个线程安全的HTTP连接池
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONN);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONN_PERHOST);

        // Validate connections after 1 sec of inactivity
        connectionManager.setValidateAfterInactivity(1000);

        idleEvictThread = new IdleConnectionMonitorThread(connectionManager);
        idleEvictThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                httpCaller.shutdown();
            }
        });
    }

    public Response call(Request request) {
        CloseableHttpClient httpClient = getHttpClient(request.getConnectionTimeout(), request.getSoTimeout());
        Response response = new Response();
        HttpRequestBase httpRequestBase = null;
        CloseableHttpResponse httpResponse = null;
        try {
            // get模式且不带上传文件
            if (request.getMethod() == HttpMethod.GET) {
                httpRequestBase = buildHttpGet(request);
            }
            // post模式且不带上传文件
            else if (request.getMethod() == HttpMethod.POST) {
                httpRequestBase = buildHttpPost(request);
            }
            httpResponse = httpClient.execute(httpRequestBase);
            HttpEntity entity = httpResponse.getEntity();
            if (httpResponse.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("http response status code is not 200. status line: "
                        + httpResponse.getStatusLine());
            }
            response.setResponseHeaders(httpResponse.getAllHeaders());
             response.setByteResult(EntityUtils.toByteArray(entity));
        } catch (Exception e) {
            if (httpRequestBase != null) {
                httpRequestBase.abort();
            }
            throw new RuntimeException("http call error:" + e);
        } finally {
            // 释放连接
            HttpClientUtils.closeQuietly(httpResponse);
        }
        return response;
    }

    /**
     * @Author qibaichao
     * @MethodName getHttpClient
     * @param connectionTimeout
     * @param soTimeout
     * @return
     * @Date 2014年9月28日
     * @Description:从连接池中取一个http连接来初始化HttpClient实例
     */
    private CloseableHttpClient getHttpClient(int connectionTimeout, int soTimeout) {

        // Create socket configuration
        SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true)
                .setSoTimeout(soTimeout == 0 ? DEFAULT_SO_TIMEOUT : soTimeout).build();
        connectionManager.setDefaultSocketConfig(socketConfig);

        // Create connection configuration
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        RequestConfig defaultRequestConfig = RequestConfig.custom().setExpectContinueEnabled(true)
                .setConnectTimeout(connectionTimeout == 0 ? DEFAULT_CONNECTIONT_IMEOUT : connectionTimeout)
                .setConnectionRequestTimeout(connectionTimeout == 0 ? DEFAULT_CONNECTIONT_IMEOUT : connectionTimeout)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager)
                .setDefaultRequestConfig(defaultRequestConfig).build();

        return httpClient;
    }

    /**
     * @Author qibaichao
     * @MethodName buildHttpGet
     * @param request
     * @return
     * @Date 2014年9月28日
     * @Description: 组装httpGet
     */
    private HttpGet buildHttpGet(Request request) {
        try {
            URIBuilder builder = null;
            if (request.getGateway() != null) {
                builder = new URIBuilder(request.getGateway());
            } else {
                builder = new URIBuilder();
                builder.setScheme(request.getScheme()).setHost(request.getHost()).setPath(request.getPath());
            }
            for (NameValuePair pair : request.getNameValuePairList()) {
                String value = pair.getValue();
                if (request.isExcludeEmptyValue() && (value == null || "".equals(value))) {
                    continue;
                }
                builder.addParameter(pair.getName(), value);
            }
            URI uri = builder.build();
            logger.info("build request uri: " + uri.toString());

            HttpGet httpGet = new HttpGet(uri);
            return httpGet;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("request uri syntax error.", e);
        }
    }

    /**
     * @Author qibaichao
     * @MethodName buildHttpPost
     * @param request
     * @return
     * @Date 2014年9月28日
     * @Description:组装HttpPost
     */
    private HttpPost buildHttpPost(Request request) {
        HttpPost httpPost;
        try {
            URIBuilder builder = null;
            if (request.getGateway() != null) {
                builder = new URIBuilder(request.getGateway());
            } else {
                builder = new URIBuilder();
                builder.setScheme(request.getScheme()).setHost(request.getHost()).setPath(request.getPath());
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(request.getNameValuePairList(), request.getCharset());
            URI uri = builder.build();
            StringBuilder sb = new StringBuilder();
            String sep = "";
            for (NameValuePair pair : request.getNameValuePairList()) {
                sb.append(sep).append(pair.toString());
                sep = " | ";
            }
            httpPost = new HttpPost(uri);
            httpPost.setEntity(entity);
            return httpPost;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("request unsupported encoding error.", e);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("request uri syntax error.", e);
        }
    }

    public void shutdown() {
        this.idleEvictThread.shutdown();
        this.connectionManager.shutdown();
    }

    /**
     * @Author qibaichao
     * @ClassName IdleConnectionMonitorThread
     * @Date 2014年9月12日
     * @Description:
     *               守护线程，定时清理过期和空闲时间超时的连接
     */
    private static class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            this.connMgr = connMgr;
            this.setDaemon(true);// 守护线程
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // 关闭过期连接
                        connMgr.closeExpiredConnections();
                        // 可选地，关闭空闲超过30秒的连接
                        connMgr.closeIdleConnections(DEFAULT_IDLE_TIMEOUT, TimeUnit.MILLISECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // 终止
            }
        }

        public void shutdown() {
            if (!shutdown) {
                shutdown = true;
                synchronized (this) {
                    notifyAll();
                }
            }
        }

    }
}
