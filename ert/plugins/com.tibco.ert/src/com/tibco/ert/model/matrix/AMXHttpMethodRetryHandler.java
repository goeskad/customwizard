package com.tibco.ert.model.matrix;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tibco.matrix.administration.command.line.PropertyConstant;

public class AMXHttpMethodRetryHandler implements HttpMethodRetryHandler, PropertyConstant {
	protected Log log = LogFactory.getLog(getClass());

	public boolean retryMethod(HttpMethod method, IOException exception, int executionCount) {
		if (log.isDebugEnabled()) {
			log.debug("*** Determining whether to retry request, retry count=" + executionCount + " ***");
		}
		if (executionCount >= 5) {
			// Do not retry if over max retry count
			return false;
		}
		if (exception instanceof NoHttpResponseException) {
			if (log.isDebugEnabled()) {
				log.debug("*** Retrying request because server dropped connection ***");
			}
			// Retry if the server dropped connection on us
			return true;
		}
		if (!method.isRequestSent()) {
			if (log.isDebugEnabled()) {
				log.debug("*** Retrying request because request not sent ***");
			}
			// Retry if the request has not been sent fully or
			// if it's OK to retry methods that have been sent
			return true;
		}
		if (exception instanceof SocketException && exception.getMessage().contains("Connection reset")) {
			if (log.isDebugEnabled()) {
				log.debug("*** Retrying request because connection reset ***");
			}
			return true;
		}
		// otherwise do not retry
		return false;
	}
}
