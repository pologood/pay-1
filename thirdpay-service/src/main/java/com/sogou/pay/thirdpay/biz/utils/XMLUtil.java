package com.sogou.pay.thirdpay.biz.utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName XMLUtil
 * @Date 2015年2月16日
 * @Description:XML 工具类
 */
public class XMLUtil {

    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点
     *
     * @param strxml
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> doXMLParse(String strxml)
            throws Exception {
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
        InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(in);
        Map<String, String> connectionInfo = new HashMap<String, String>();

        Element root = document.getRootElement();
        Iterator<Element> rootIter = root.elementIterator();
        while (rootIter.hasNext()) {
            Element ele = rootIter.next();
            String value = ele.getText();
            String key = ele.getName();
            connectionInfo.put(key, value);
        }
        return connectionInfo;
    }
}
