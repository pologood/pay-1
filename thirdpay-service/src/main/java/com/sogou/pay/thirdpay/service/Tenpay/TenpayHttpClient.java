package com.sogou.pay.thirdpay.service.Tenpay;


import com.sogou.pay.common.http.client.HttpService;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.thirdpay.biz.utils.HttpClientUtil;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by xiepeidong on 2016/1/27.
 */
public class TenpayHttpClient {

    private static final String JKS_CA_FILENAME = "tenpay_cacert.jks";
    private static final String JKS_CA_ALIAS = "tenpay";
    private static final String JKS_CA_PASSWORD = "";
    private static final String SunX509 = "SunX509";
    private static final String JKS = "JKS";
    private static final String PKCS12 = "PKCS12";
    private static final String TLS = "TLS";

    /**
     * ca证书文件
     */
    private String caCertFile;

    /**
     * 我方证书文件
     */
    private String myCertFile;

    /**
     * 证书密码
     */
    private String certPasswd;

    private String charset = "UTF-8";

    private SSLContext sslContext;

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setCertFile(String myCertFile, String certPasswd, String caCertFile) {
        this.myCertFile = myCertFile;
        this.certPasswd = certPasswd;
        this.caCertFile = caCertFile;
        this.sslContext = createSslContext(this.myCertFile, certPasswd, this.caCertFile);
    }

    private SSLContext createSslContext(String myCertFile, String certPasswd, String caCertFile) {
        try {
            File pem_caCertFile = new File(caCertFile);
            File jks_caCertFile = new File(pem_caCertFile.getParentFile(), TenpayHttpClient.JKS_CA_FILENAME);
            if (!jks_caCertFile.isFile()) {
                X509Certificate cert = (X509Certificate) HttpClientUtil
                        .getCertificate(pem_caCertFile);
                FileOutputStream out = new FileOutputStream(jks_caCertFile);
                // store jks file
                HttpClientUtil.storeCACert(cert, TenpayHttpClient.JKS_CA_ALIAS,
                        TenpayHttpClient.JKS_CA_PASSWORD, out);
                out.close();
            }
            // ca cert
            FileInputStream fis = new FileInputStream(jks_caCertFile);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TenpayHttpClient.SunX509);
            KeyStore trustKeyStore = KeyStore.getInstance(TenpayHttpClient.JKS);
            trustKeyStore.load(fis, TenpayHttpClient.JKS_CA_PASSWORD.toCharArray());
            tmf.init(trustKeyStore);
            fis.close();

            // my cert
            fis = new FileInputStream(myCertFile);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(TenpayHttpClient.SunX509);
            KeyStore ks = KeyStore.getInstance(TenpayHttpClient.PKCS12);
            ks.load(fis, certPasswd.toCharArray());
            kmf.init(ks, certPasswd.toCharArray());
            fis.close();

            SecureRandom rand = new SecureRandom();
            SSLContext ctx = SSLContext.getInstance(TenpayHttpClient.TLS);
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), rand);
            return ctx;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Result doGet(String url, Map<String, String> paramMap) {
        if (url.startsWith("https:")) {
            return HttpService.getInstance().doGet(url, paramMap, this.charset, this.sslContext);
        } else {
            return HttpService.getInstance().doGet(url, paramMap, this.charset, null);
        }
    }

    public Result doGet(String url, String paramString) {
        if (url.startsWith("https:")) {
            return HttpService.getInstance().doGet(url, paramString, this.charset, this.sslContext);
        } else {
            return HttpService.getInstance().doGet(url, paramString, this.charset, null);
        }
    }

    public Result doPost(String url, Map<String, String> paramMap) {
        if (url.startsWith("https:")) {
            return HttpService.getInstance().doPost(url, paramMap, this.charset, this.sslContext);
        } else {
            return HttpService.getInstance().doPost(url, paramMap, this.charset, null);
        }
    }

    public Result doPost(String url, String paramString) {
        if (url.startsWith("https:")) {
            return HttpService.getInstance().doPost(url, paramString, this.charset, this.sslContext);
        } else {
            return HttpService.getInstance().doPost(url, paramString, this.charset, null);
        }
    }
}
