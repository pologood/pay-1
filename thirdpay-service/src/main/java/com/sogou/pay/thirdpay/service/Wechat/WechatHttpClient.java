package com.sogou.pay.thirdpay.service.Wechat;


import com.sogou.pay.common.http.client.HttpService;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.thirdpay.biz.utils.HttpClientUtil;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by xiepeidong on 2016/2/15.
 */
public class WechatHttpClient {

    private static final String JKS_CA_FILENAME = "wechat_cacert.jks";
    private static final String JKS_CA_ALIAS = "wechat";
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
            File jks_caCertFile = new File(pem_caCertFile.getParentFile(), WechatHttpClient.JKS_CA_FILENAME);
            if (!jks_caCertFile.isFile()) {
                X509Certificate cert = (X509Certificate) HttpClientUtil
                        .getCertificate(pem_caCertFile);
                FileOutputStream out = new FileOutputStream(jks_caCertFile);
                // store jks file
                HttpClientUtil.storeCACert(cert, WechatHttpClient.JKS_CA_ALIAS,
                        WechatHttpClient.JKS_CA_PASSWORD, out);
                out.close();
            }
            // ca cert
            FileInputStream fis = new FileInputStream(jks_caCertFile);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(WechatHttpClient.SunX509);
            KeyStore trustKeyStore = KeyStore.getInstance(WechatHttpClient.JKS);
            trustKeyStore.load(fis, WechatHttpClient.JKS_CA_PASSWORD.toCharArray());
            tmf.init(trustKeyStore);
            fis.close();

            // my cert
            fis = new FileInputStream(myCertFile);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(WechatHttpClient.SunX509);
            KeyStore ks = KeyStore.getInstance(WechatHttpClient.PKCS12);
            ks.load(fis, certPasswd.toCharArray());
            kmf.init(ks, certPasswd.toCharArray());
            fis.close();

            SecureRandom rand = new SecureRandom();
            SSLContext ctx = SSLContext.getInstance(WechatHttpClient.TLS);
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

    public Result doPost(String url, Map<String, String> paramMap) {
        if (url.startsWith("https:")) {
            return HttpService.getInstance().doPost(url, paramMap, this.charset, this.sslContext);
        } else {
            return HttpService.getInstance().doPost(url, paramMap, this.charset, null);
        }
    }

    public Result doPost(String url, String paramMap) {
        if (url.startsWith("https:")) {
            return HttpService.getInstance().doPost(url, paramMap, this.charset, this.sslContext);
        } else {
            return HttpService.getInstance().doPost(url, paramMap, this.charset, null);
        }
    }
}
