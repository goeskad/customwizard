package com.tibco.configtool.internal.support;

import java.io.IOException;
import java.io.Writer;

import org.apache.tools.ant.DefaultLogger;

public class AntLoggerWithProgressMonitor extends DefaultLogger {
	private Writer progressMonitor;

	public AntLoggerWithProgressMonitor(Writer progressMonitor) {
		this.progressMonitor = progressMonitor;
	}
	
	protected void log(String message) {
		try {
			progressMonitor.write(message);
		} catch (IOException e) {
			//won't happen
		}
	}
}
