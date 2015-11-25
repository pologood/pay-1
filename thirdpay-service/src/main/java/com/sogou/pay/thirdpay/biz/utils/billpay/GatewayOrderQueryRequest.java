/**
 * GatewayOrderQueryRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sogou.pay.thirdpay.biz.utils.billpay;

import org.apache.axis.description.TypeDesc;

public class GatewayOrderQueryRequest implements java.io.Serializable {
    private String endTime;

    private String inputCharset;

    private String merchantAcctId;

    private String orderId;

    private int queryMode;

    private int queryType;

    private String requestPage;

    private String signMsg;

    private int signType;

    private String startTime;

    private String version;

    public GatewayOrderQueryRequest() {
    }

    public GatewayOrderQueryRequest(
            String endTime,
            String inputCharset,
            String merchantAcctId,
            String orderId,
            int queryMode,
            int queryType,
            String requestPage,
            String signMsg,
            int signType,
            String startTime,
            String version) {
           this.endTime = endTime;
           this.inputCharset = inputCharset;
           this.merchantAcctId = merchantAcctId;
           this.orderId = orderId;
           this.queryMode = queryMode;
           this.queryType = queryType;
           this.requestPage = requestPage;
           this.signMsg = signMsg;
           this.signType = signType;
           this.startTime = startTime;
           this.version = version;
    }


    /**
     * Gets the endTime value for this GatewayOrderQueryRequest.
     *
     * @return endTime
     */
    public String getEndTime() {
        return endTime;
    }


    /**
     * Sets the endTime value for this GatewayOrderQueryRequest.
     *
     * @param endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    /**
     * Gets the inputCharset value for this GatewayOrderQueryRequest.
     *
     * @return inputCharset
     */
    public String getInputCharset() {
        return inputCharset;
    }


    /**
     * Sets the inputCharset value for this GatewayOrderQueryRequest.
     *
     * @param inputCharset
     */
    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }


    /**
     * Gets the merchantAcctId value for this GatewayOrderQueryRequest.
     *
     * @return merchantAcctId
     */
    public String getMerchantAcctId() {
        return merchantAcctId;
    }


    /**
     * Sets the merchantAcctId value for this GatewayOrderQueryRequest.
     *
     * @param merchantAcctId
     */
    public void setMerchantAcctId(String merchantAcctId) {
        this.merchantAcctId = merchantAcctId;
    }


    /**
     * Gets the orderId value for this GatewayOrderQueryRequest.
     *
     * @return orderId
     */
    public String getOrderId() {
        return orderId;
    }


    /**
     * Sets the orderId value for this GatewayOrderQueryRequest.
     *
     * @param orderId
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    /**
     * Gets the queryMode value for this GatewayOrderQueryRequest.
     *
     * @return queryMode
     */
    public int getQueryMode() {
        return queryMode;
    }


    /**
     * Sets the queryMode value for this GatewayOrderQueryRequest.
     *
     * @param queryMode
     */
    public void setQueryMode(int queryMode) {
        this.queryMode = queryMode;
    }


    /**
     * Gets the queryType value for this GatewayOrderQueryRequest.
     *
     * @return queryType
     */
    public int getQueryType() {
        return queryType;
    }


    /**
     * Sets the queryType value for this GatewayOrderQueryRequest.
     *
     * @param queryType
     */
    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }


    /**
     * Gets the requestPage value for this GatewayOrderQueryRequest.
     *
     * @return requestPage
     */
    public String getRequestPage() {
        return requestPage;
    }


    /**
     * Sets the requestPage value for this GatewayOrderQueryRequest.
     *
     * @param requestPage
     */
    public void setRequestPage(String requestPage) {
        this.requestPage = requestPage;
    }


    /**
     * Gets the signMsg value for this GatewayOrderQueryRequest.
     *
     * @return signMsg
     */
    public String getSignMsg() {
        return signMsg;
    }


    /**
     * Sets the signMsg value for this GatewayOrderQueryRequest.
     *
     * @param signMsg
     */
    public void setSignMsg(String signMsg) {
        this.signMsg = signMsg;
    }


    /**
     * Gets the signType value for this GatewayOrderQueryRequest.
     *
     * @return signType
     */
    public int getSignType() {
        return signType;
    }


    /**
     * Sets the signType value for this GatewayOrderQueryRequest.
     *
     * @param signType
     */
    public void setSignType(int signType) {
        this.signType = signType;
    }


    /**
     * Gets the startTime value for this GatewayOrderQueryRequest.
     *
     * @return startTime
     */
    public String getStartTime() {
        return startTime;
    }


    /**
     * Sets the startTime value for this GatewayOrderQueryRequest.
     *
     * @param startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    /**
     * Gets the version value for this GatewayOrderQueryRequest.
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this GatewayOrderQueryRequest.
     *
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GatewayOrderQueryRequest)) return false;
        GatewayOrderQueryRequest other = (GatewayOrderQueryRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.endTime==null && other.getEndTime()==null) ||
             (this.endTime!=null &&
              this.endTime.equals(other.getEndTime()))) &&
            ((this.inputCharset==null && other.getInputCharset()==null) ||
             (this.inputCharset!=null &&
              this.inputCharset.equals(other.getInputCharset()))) &&
            ((this.merchantAcctId==null && other.getMerchantAcctId()==null) ||
             (this.merchantAcctId!=null &&
              this.merchantAcctId.equals(other.getMerchantAcctId()))) &&
            ((this.orderId==null && other.getOrderId()==null) ||
             (this.orderId!=null &&
              this.orderId.equals(other.getOrderId()))) &&
            this.queryMode == other.getQueryMode() &&
            this.queryType == other.getQueryType() &&
            ((this.requestPage==null && other.getRequestPage()==null) ||
             (this.requestPage!=null &&
              this.requestPage.equals(other.getRequestPage()))) &&
            ((this.signMsg==null && other.getSignMsg()==null) ||
             (this.signMsg!=null &&
              this.signMsg.equals(other.getSignMsg()))) &&
            this.signType == other.getSignType() &&
            ((this.startTime==null && other.getStartTime()==null) ||
             (this.startTime!=null &&
              this.startTime.equals(other.getStartTime()))) &&
            ((this.version==null && other.getVersion()==null) ||
             (this.version!=null &&
              this.version.equals(other.getVersion())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getEndTime() != null) {
            _hashCode += getEndTime().hashCode();
        }
        if (getInputCharset() != null) {
            _hashCode += getInputCharset().hashCode();
        }
        if (getMerchantAcctId() != null) {
            _hashCode += getMerchantAcctId().hashCode();
        }
        if (getOrderId() != null) {
            _hashCode += getOrderId().hashCode();
        }
        _hashCode += getQueryMode();
        _hashCode += getQueryType();
        if (getRequestPage() != null) {
            _hashCode += getRequestPage().hashCode();
        }
        if (getSignMsg() != null) {
            _hashCode += getSignMsg().hashCode();
        }
        _hashCode += getSignType();
        if (getStartTime() != null) {
            _hashCode += getStartTime().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static TypeDesc typeDesc =
        new TypeDesc(GatewayOrderQueryRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://gatewayquery.dto.domain.seashell.bill99.com", "GatewayOrderQueryRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "endTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inputCharset");
        elemField.setXmlName(new javax.xml.namespace.QName("", "inputCharset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantAcctId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantAcctId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "orderId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "queryMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "queryType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestPage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "requestPage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signMsg");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signMsg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("", "version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
