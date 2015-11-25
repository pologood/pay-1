package com.sogou.pay.common.utils;

import com.thoughtworks.xstream.XStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 用于封装xml参数
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-2
 * Time: 下午9:08
 */
public class XMLUtil {
    private static final Logger logger = LoggerFactory.getLogger(XMLUtil.class);


    //初始化xstream
    private static final XStream xstream;

    static {
        xstream = new XStream();
        //注册将pojo转为map的coverter
        xstream.registerConverter(new PojoMapConverter());
    }

    /**
     * 将map转换为xml
     *
     * @param rootNode root节点的名称
     * @param map      要转换的map
     * @return
     */
    public static Document mapToXml(String rootNode, Map<String, Object> map) {
        if (StringUtil.isBlank(rootNode)) {
            throw new RuntimeException("xml rootNode may not be null");
        }
        Document document = DocumentHelper.createDocument();

        Element rootElement = document.addElement(rootNode);

        if (map == null || map.isEmpty()) {
            return document;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String value = entry.getValue() == null ? StringUtil.EMPTY_STRING : entry.getValue().toString();
            Element element = rootElement.addElement(entry.getKey());
            element.setText(value);
        }
        return document;
    }

    /**
     * 将map转换为xml
     *
     * @param rootNode root节点的名称
     * @param map      要转换的map
     * @return
     */
    public static String mapToXmlString(String rootNode, Map<String, Object> map) {
        Document document = mapToXml(rootNode, map);
        return document.asXML();
    }


    /**
     * 将xml转为对象
     * 目前只支持将整个xml转换为简单的POJO或者转换为简单的map
     * 暂不支持复杂map，list的转换
     *
     * @param xml  xml的内容
     * @param type 要的到的Bena.class
     * @param <T>  泛型支持
     * @return 转换后的bean
     */
    public static <T> T xmlToBean(final String xml, final Class<T> type) {
        try {
            //由于使用重命名功能xterm.alias(rootNodeName, type);一旦设置重命名就无法在修改
            //所以这里选择将xml的root节点修改为要转换的type的name，经测试这样做基本没有性能问题
            //但如果使用xstream.alias(rootNodeName, type);就需要每次初始化一个XStream()，性能下降3倍以上
            Document document = DocumentHelper.parseText(xml);
            document.getRootElement().setName(type.getName());
            String newXml = document.asXML();
            //转换
            return (T) xstream.fromXML(newXml);
        } catch (DocumentException e) {
            logger.error("xmlToBean fail,xml:" + xml + "; type=" + type, e);
            throw new RuntimeException("xml to bean error", e);
        }
    }


}
