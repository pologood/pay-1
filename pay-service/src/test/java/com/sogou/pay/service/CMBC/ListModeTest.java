package com.sogou.pay.service.CMBC;

import com.sogou.pay.common.http.utils.HttpUtil;

import java.util.Map;
import java.util.Properties;

/**
 * Created by qibaichao on 2015/9/14.
 */
public class ListModeTest {

    /**
     * 生成请求报文
     *
     * @return
     */
    public static String getRequestStr() {
        // 构造查询余额的请求报文
        XmlPacket xmlPkt = new XmlPacket("ListMode", "dmcp1");
        Map mpAccInfo = new Properties();
        mpAccInfo.put("BUSCOD", "N02031");
        xmlPkt.putProperty("SDKMDLSTX", mpAccInfo);
        return xmlPkt.toXmlString();
    }


    public static void main(String[] args) {
        try {

            // 生成请求报文
            String data = getRequestStr();

            // 连接前置机，发送请求报文，获得返回报文
            String result = HttpUtil.sendPost("http://127.0.0.1:8088", data);
            System.out.println(result);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
