package com.sogou.pay.thirdpay.service.CMBC;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;
import java.util.Properties;


/**
 * 招行XML报文解析类
 *
 */
public class SaxHandler extends DefaultHandler {
    int layer = 0;
    String curSectionName;
    String curKey;
    String curValue;
    XmlPacket pktData;
    Map mpRecord;

    public SaxHandler(XmlPacket data) {
        curSectionName = "";
        curKey = "";
        curValue = "";
        pktData = data;
        mpRecord = new Properties();
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        layer++;
        if (layer == 2) {
            curSectionName = qName;
        } else if (layer == 3) {
            curKey = qName;
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (layer == 2) {
            pktData.putProperty(curSectionName, mpRecord);
            mpRecord = new Properties();
        } else if (layer == 3) {
            mpRecord.put(curKey, curValue);
            if (curSectionName.equals("INFO")) {
                if (curKey.equals("FUNNAM")) {
                    pktData.setFUNNAM(curValue);
                } else if (curKey.equals("LGNNAM")) {
                    pktData.setLGNNAM(curValue);
                } else if (curKey.equals("RETCOD")) {
                    pktData.setRETCOD(curValue);
                } else if (curKey.equals("ERRMSG")) {
                    pktData.setERRMSG(curValue);
                }
            }
        }
        curValue = "";
        layer--;
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (layer == 3) {
            String value = new String(ch, start, length);
            if (ch.equals("\n")) {
                curValue += "\r\n";
            } else {
                curValue += value;
            }
        }
    }
}
