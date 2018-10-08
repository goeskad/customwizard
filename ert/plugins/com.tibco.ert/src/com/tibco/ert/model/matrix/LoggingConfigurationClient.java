package com.tibco.ert.model.matrix;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.xmlbeans.XmlBeansTypeRegistry;

import com.tibco.matrix.administration.command.line.ant.AMXAdminTask;
import com.tibco.matrix.administration.command.line.client.loggingconfiguration.loggingconfiguration;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class LoggingConfigurationClient {
    private static XFireProxyFactory proxyFactory = new XFireProxyFactory();
    
    public static loggingconfiguration loggingconfigurationstub = new LoggingConfigurationClient().getloggingconfiguration();
    
	public LoggingConfigurationClient() {
        endpoints = new HashMap();
        create0();
        Endpoint loggingconfigurationEP = service0.addEndpoint(new QName("http://matrix.tibco.com/admin/server/services/loggingconfiguration/messages", "loggingconfiguration"), new QName(
                "http://matrix.tibco.com/admin/server/services/loggingconfiguration/messages", "loggingConfigurationSOAP"), (new StringBuilder(String.valueOf(AMXAdminTask.ADMIN_URL))).append(
                "/services/loggingconfiguration").toString());
        endpoints.put(new QName("http://matrix.tibco.com/admin/server/services/loggingconfiguration/messages", "loggingconfiguration"), loggingconfigurationEP);
        Endpoint loggingconfigurationLocalEndpointEP = service0.addEndpoint(new QName("http://matrix.tibco.com/admin/server/services/loggingconfiguration/messages",
                "loggingconfigurationLocalEndpoint"), new QName("http://matrix.tibco.com/admin/server/services/loggingconfiguration/messages", "loggingconfigurationLocalBinding"),
                "xfire.local://loggingconfiguration");
        endpoints.put(new QName("http://matrix.tibco.com/admin/server/services/loggingconfiguration/messages", "loggingconfigurationLocalEndpoint"), loggingconfigurationLocalEndpointEP);
    }

    public Object getEndpoint(Endpoint endpoint) {
        try {
            return proxyFactory.create(endpoint.getBinding(), endpoint.getUrl());
        } catch (MalformedURLException e) {
            throw new XFireRuntimeException("Invalid URL", e);
        }
    }

    public Object getEndpoint(QName name) {
        Endpoint endpoint = (Endpoint) endpoints.get(name);
        if (endpoint == null)
            throw new IllegalStateException("No such endpoint!");
        else
            return getEndpoint(endpoint);
    }

    public Collection getEndpoints() {
        return endpoints.values();
    }

    private void create0() {
        org.codehaus.xfire.transport.TransportManager tm = XFireFactory.newInstance().getXFire().getTransportManager();
        HashMap props = new HashMap();
        props.put("annotations.allow.interface", Boolean.valueOf(true));
        HttpClientParams params = new HttpClientParams();
        params.setParameter("http.useragent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; XFire Client +http://xfire.codehaus.org)");
        params.setBooleanParameter("http.protocol.expect-continue", false);
        params.setVersion(HttpVersion.HTTP_1_1);
        props.put("httpClient.params", params);
        props.put("Cookie", AMXAdminTask.COOKIE);
        AnnotationServiceFactory asf = new AnnotationServiceFactory(new Jsr181WebAnnotations(), tm, new AegisBindingProvider(new XmlBeansTypeRegistry()));
        asf.setBindingCreationEnabled(false);
        service0 = asf.create(loggingconfiguration.class, props);
        asf.createSoap11Binding(service0, new QName("http://matrix.tibco.com/admin/server/services/loggingconfiguration/messages", "loggingConfigurationSOAP"), "http://schemas.xmlsoap.org/soap/http");
        asf.createSoap11Binding(service0, new QName("http://matrix.tibco.com/admin/server/services/loggingconfiguration/messages", "loggingconfigurationLocalBinding"), "urn:xfire:transport:local");
    }

    public loggingconfiguration getloggingconfiguration() {
        return (loggingconfiguration) getEndpoint(new QName("http://matrix.tibco.com/admin/server/services/loggingconfiguration/messages", "loggingconfiguration"));
    }

    public loggingconfiguration getloggingconfiguration(String url) {
        loggingconfiguration var = getloggingconfiguration();
        Client.getInstance(var).setUrl(url);
        return var;
    }

    public loggingconfiguration getloggingconfigurationLocalEndpoint() {
        return (loggingconfiguration) getEndpoint(new QName("http://matrix.tibco.com/admin/server/services/loggingconfiguration/messages", "loggingconfigurationLocalEndpoint"));
    }

    public loggingconfiguration getloggingconfigurationLocalEndpoint(String url) {
        loggingconfiguration var = getloggingconfigurationLocalEndpoint();
        Client.getInstance(var).setUrl(url);
        return var;
    }

    private HashMap endpoints;

    private Service service0;

}
