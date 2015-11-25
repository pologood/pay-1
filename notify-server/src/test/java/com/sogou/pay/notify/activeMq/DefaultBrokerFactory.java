package com.sogou.pay.notify.activeMq;

import org.apache.activemq.broker.BrokerFactoryHandler;
import org.apache.activemq.protobuf.compiler.IntrospectionSupport;
import org.apache.activemq.util.URISupport;

import java.net.URI;
import java.util.HashMap;

public class DefaultBrokerFactory implements BrokerFactoryHandler {

    public static void main(String args[]) {

        try {
            DefaultBrokerFactory  defaultBrokerFactory = new DefaultBrokerFactory();
            BrokerService brokerService =defaultBrokerFactory.createBroker(new URI("broker:tcp://localhost:61616"));
            brokerService.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public DefaultBrokerFactory() {
    }

    public BrokerService createBroker(URI brokerURI) throws Exception {
        URISupport.CompositeData compositeData = URISupport.parseComposite(brokerURI);
        HashMap params = new HashMap(compositeData.getParameters());
        BrokerService brokerService = new BrokerService();
        IntrospectionSupport.setProperties(brokerService, params);
        if(!params.isEmpty()) {
            String var7 = "There are " + params.size() + " Broker options that couldn\'t be set on the BrokerService." + " Check the options are spelled correctly." + " Unknown parameters=[" + params + "]." + " This BrokerService cannot be started.";
            throw new IllegalArgumentException(var7);
        } else {
            if(compositeData.getPath() != null) {
                brokerService.setBrokerName(compositeData.getPath());
            }

            URI[] components = compositeData.getComponents();

            for(int i = 0; i < components.length; ++i) {
                if("network".equals(components[i].getScheme())) {
                    brokerService.addNetworkConnector(components[i].getSchemeSpecificPart());
                } else if("proxy".equals(components[i].getScheme())) {
                    brokerService.addProxyConnector(components[i].getSchemeSpecificPart());
                } else {
                    brokerService.addConnector(components[i]);
                }
            }

            return brokerService;
        }
    }
}