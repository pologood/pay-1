/**
 * GatewayOrderQueryServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sogou.pay.thirdpay.biz.utils.billpay;

import javax.xml.rpc.ServiceException;

public class GatewayOrderQueryServiceLocator extends org.apache.axis.client.Service implements GatewayOrderQueryService {

    public GatewayOrderQueryServiceLocator() {
    }


    public GatewayOrderQueryServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GatewayOrderQueryServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for gatewayOrderQuery
    private String gatewayOrderQuery_address = "http://sandbox.99bill.com/apipay/services/gatewayOrderQuery";

    public String getgatewayOrderQueryAddress() {
        return gatewayOrderQuery_address;
    }

    // The WSDD service name defaults to the port name.
    private String gatewayOrderQueryWSDDServiceName = "gatewayOrderQuery";

    public String getgatewayOrderQueryWSDDServiceName() {
        return gatewayOrderQueryWSDDServiceName;
    }

    public void setgatewayOrderQueryWSDDServiceName(String name) {
        gatewayOrderQueryWSDDServiceName = name;
    }

    public GatewayOrderQuery getgatewayOrderQuery() throws ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(gatewayOrderQuery_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new ServiceException(e);
        }
        return getgatewayOrderQuery(endpoint);
    }

    public GatewayOrderQuery getgatewayOrderQuery(java.net.URL portAddress) throws ServiceException {
        try {
            GatewayOrderQuerySoapBindingStub _stub = new GatewayOrderQuerySoapBindingStub(portAddress, this);
            _stub.setPortName(getgatewayOrderQueryWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setgatewayOrderQueryEndpointAddress(String address) {
        gatewayOrderQuery_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws ServiceException {
        try {
            if (GatewayOrderQuery.class.isAssignableFrom(serviceEndpointInterface)) {
                GatewayOrderQuerySoapBindingStub _stub = new GatewayOrderQuerySoapBindingStub(new java.net.URL(gatewayOrderQuery_address), this);
                _stub.setPortName(getgatewayOrderQueryWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new ServiceException(t);
        }
        throw new ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("gatewayOrderQuery".equals(inputPortName)) {
            return getgatewayOrderQuery();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://sandbox.99bill.com/apipay/services/gatewayOrderQuery", "GatewayOrderQueryService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://sandbox.99bill.com/apipay/services/gatewayOrderQuery", "gatewayOrderQuery"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws ServiceException {

if ("gatewayOrderQuery".equals(portName)) {
            setgatewayOrderQueryEndpointAddress(address);
        }
        else
{ // Unknown Port Name
            throw new ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
