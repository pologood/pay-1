package com.sogou.pay.service.CMBC;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by qibaichao on 2015/6/18.
 */
public class GetTransInfoTest {

    /**
     * 生成请求报文
     *
     * @return
     */
    public static String getRequestStr() {
        // 构造查询余额的请求报文
        XmlPacket xmlPkt = new XmlPacket("GetTransInfo", "dmcp1");
        Map mpAccInfo = new Properties();
        mpAccInfo.put("BBKNBR", "59");
        mpAccInfo.put("ACCNBR", "591902896010504");
        mpAccInfo.put("BGNDAT", "20141111");
        mpAccInfo.put("ENDDAT", "20141111");
        xmlPkt.putProperty("SDKTSINFX", mpAccInfo);
        return xmlPkt.toXmlString();
    }

    /**
     * 处理返回的结果
     *
     * @param result
     */
    public static void processResult(String result) {
        if (result != null && result.length() > 0) {
            XmlPacket pktRsp = XmlPacket.valueOf(result);
            if (pktRsp != null) {
                if (pktRsp.isError()) {
                    System.out.println("取信息失败：" + pktRsp.getERRMSG());
                } else {
                    Map propAcc = pktRsp.getProperty("NTQTSINFZ", 0);
//                    System.out.println(pktRsp.toXmlString());
                }
            } else {
                System.out.println("响应报文解析失败");
            }
        }
    }


    public static void main(String[] args) {
        try {

            // 生成请求报文
            String data = getRequestStr();

            // 连接前置机，发送请求报文，获得返回报文
            String result = SocketRequest.sendRequest(data);

            // 处理返回的结果
            processResult(result);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
