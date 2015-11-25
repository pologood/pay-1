package com.sogou.pay.service.CMBC;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;


/**
 * HTTPS通讯范例: 查询交易明细
 *
 * @author 徐蓓
 */
public class HttpsRequest {
    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    /**
     * 生成请求报文
     *
     * @return
     */
    private String getRequestStr() {
        // 构造查询账户明细的请求报文
        XmlPacket xmlPkt = new XmlPacket("GetTransInfo", "USRA01");
        Map mpAccInfo = new Properties();
        mpAccInfo.put("BBKNBR", "57");
        mpAccInfo.put("ACCNBR", "571905400910411");
        mpAccInfo.put("BGNDAT", "20100422");
        mpAccInfo.put("ENDDAT", "20100501");
        xmlPkt.putProperty("SDKTSINFX", mpAccInfo);
        return xmlPkt.toXmlString();
    }

    /**
     * 连接前置机，发送请求报文，获得返回报文
     *
     * @param data
     * @return
     * @throws java.security.KeyManagementException
     * @throws java.security.NoSuchAlgorithmException
     */
    private String sendRequest(String data) {
        String result = "";
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
                    new java.security.SecureRandom());

            URL url;
            url = new URL("https://localhost:443");

            HttpsURLConnection conn;
            conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            OutputStream os;
            os = conn.getOutputStream();
            os.write(data.toString().getBytes("gbk"));
            os.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result += line;
            }

            System.out.println(result);
            br.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 处理返回的结果
     *
     * @param result
     */
    private void processResult(String result) {
        if (result != null && result.length() > 0) {
            XmlPacket pktRsp = XmlPacket.valueOf(result);
            if (pktRsp != null) {
                if (pktRsp.isError()) {
                    System.out.println("取账户交易明细失败：" + pktRsp.getERRMSG());
                } else {
                    int size = pktRsp.getSectionSize("NTQTSINFZ");
                    System.out.println("查询结果明细数："+size);
                    for(int i=0; i<size; i++){
                        Map propDtl = pktRsp.getProperty("NTQTSINFZ", i);
                        System.out.println("交易日:" + propDtl.get("ETYDAT")
                                + " 交易时间:"+ propDtl.get("ETYTIM")
                                + " 流程实例号:" + propDtl.get("REQNBR")
                                + " 对方账号:" + propDtl.get("RPYACC")
                                + " 借方金额:" + propDtl.get("TRSAMTD")
                                + " 贷方金额:" + propDtl.get("TRSAMTC"));
                    }
                }
            } else {
                System.out.println("响应报文解析失败");
            }
        }
    }

    public static void main(String[] args) {
        try {
            HttpsRequest request = new HttpsRequest();

            // 生成请求报文
//            String data = request.getRequestStr();

            String data ="<?xml version='1.0' encoding = 'utf-8'?><CMBSDKPGK><INFO><FUNNAM>GetTransInfo</FUNNAM><DATTYP>2</DATTYP><LGNNAM>dmcp1</LGNNAM></INFO><SDKTSINFX><ENDDAT>20141129</ENDDAT><ACCNBR>591902896010504</ACCNBR><BBKNBR>59</BBKNBR><BGNDAT>20141129</BGNDAT></SDKTSINFX></CMBSDKPGK>";
            // 连接前置机，发送请求报文，获得返回报文
            String result = request.sendRequest(data);

            // 处理返回的结果
            request.processResult(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}