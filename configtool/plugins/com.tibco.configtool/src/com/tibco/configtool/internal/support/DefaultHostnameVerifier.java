package com.tibco.configtool.internal.support;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class DefaultHostnameVerifier implements HostnameVerifier{
	public boolean verify(String host, SSLSession session) {
		return true;
	}
}
