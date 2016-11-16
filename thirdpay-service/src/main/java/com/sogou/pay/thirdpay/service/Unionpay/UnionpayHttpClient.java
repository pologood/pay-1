package com.sogou.pay.thirdpay.service.Unionpay;


import com.sogou.pay.common.http.HttpService;
import com.sogou.pay.common.types.Result;
import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by xiepeidong on 2016/6/21.
 */
public class UnionpayHttpClient {

    private static final String TLS = "TLS";

    private String charset = "UTF-8";

    private SSLContext sslContext;

    public void setCharset(String charset) {
        this.charset = charset;
    }

    UnionpayHttpClient(String charset){
        this.charset = charset;
        this.sslContext = createSslContext();
    }

    private SSLContext createSslContext() {
        try {
            TrustManager[] tms = new TrustManager[1];
            tms[0] = new TrustAll();
            SSLContext ctx = SSLContext.getInstance(UnionpayHttpClient.TLS);
            ctx.init(null, tms, null);
            return ctx;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Result<?> doGet(String url, Map<String, ?> paramMap) {
        if (url.startsWith("https:")) {
            return HttpService.getInstance().doGet(url, paramMap, this.charset, this.sslContext);
        } else {
            return HttpService.getInstance().doGet(url, paramMap, this.charset, null);
        }
    }

    public Result<?> doGet(String url, String paramString) {
        if (url.startsWith("https:")) {
            return HttpService.getInstance().doGet(url, paramString, this.charset, this.sslContext);
        } else {
            return HttpService.getInstance().doGet(url, paramString, this.charset, null);
        }
    }

    public Result<?> doPost(String url, Map<String, ?> paramMap) {
        if (url.startsWith("https:")) {
            return HttpService.getInstance().doPost(url, paramMap, this.charset, this.sslContext);
        } else {
            return HttpService.getInstance().doPost(url, paramMap, this.charset, null);
        }
    }

    public Result<?> doPost(String url, String paramString) {
        if (url.startsWith("https:")) {
            return HttpService.getInstance().doPost(url, paramString, this.charset, this.sslContext);
        } else {
            return HttpService.getInstance().doPost(url, paramString, this.charset, null);
        }
    }

    static class TrustAll implements TrustManager,X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }
    }
}
