package com.tibco.configtool.utils;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import com.tibco.trinity.runtime.core.connector.ConnectorUtils;
import com.tibco.trinity.runtime.core.provider.identity.IdentityTrustConnection;
import com.tibco.trinity.runtime.core.provider.identity.ssl.MakeSocketFactory;
import com.tibco.trinity.runtime.core.sspi.ProviderNames;

public class SSLUtils {
	public static Map<String, String> getSslProperties(String keystoreLocation, String keystorePassword, String keystoreType) {
		Map<String, String> props = new HashMap<String, String>();

		// Required properties
		props.put("com.tibco.trinity.runtime.core.provider.identity.trust.trustStoreServiceProvider",
		        "class:com.tibco.trinity.runtime.core.provider.credential.keystore");
		props.put("com.tibco.trinity.runtime.core.provider.identity.trust.enableTrustStoreAccess", "true");
		props.put("com.tibco.trinity.runtime.core.provider.credential.keystore.truststore.keyStoreLocation", keystoreLocation);
		props.put("com.tibco.trinity.runtime.core.provider.credential.keystore.truststore.keyStorePassword", keystorePassword);
		props.put("com.tibco.trinity.runtime.core.provider.credential.keystore.truststore.keyStoreType", keystoreType);

		return props;
	}

	public static IdentityTrustConnection getIdentityTrustConnection(Map<String, String> sslParams) throws Exception {
		return ConnectorUtils.lookup(ConnectorUtils.LOOKUP_JVM_PREFIX + ProviderNames.trust.toString(), sslParams);
	}

	public static void setDefaultHttps(String trustStoreFile, String trustStorePassword, String trustStoreType) throws Exception {
		IdentityTrustConnection trustConnection = getIdentityTrustConnection(getSslProperties(trustStoreFile, trustStorePassword,
		        trustStoreType));
		HttpsURLConnection.setDefaultSSLSocketFactory(trustConnection.getSSLContext().getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(trustConnection.getHostnameVerifier());
	}

	public static String getSocketFactoryClassName(String keystoreLocation, String keystorePassword, String keystoreType)
	        throws Exception {
		SSLContext context = getIdentityTrustConnection(getSslProperties(keystoreLocation, keystorePassword, keystoreType))
		        .getSSLContext();

		final ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
		String socketFactoryClassName = null;
		try {
			final ClassLoader combinedLoader = MakeSocketFactory.getCombinedLoader(currentCL);
			Thread.currentThread().setContextClassLoader(combinedLoader);

			socketFactoryClassName = MakeSocketFactory.makeSocketFactory(context.getSocketFactory());
		} finally {
			Thread.currentThread().setContextClassLoader(currentCL);
		}
		return socketFactoryClassName;
	}
}
