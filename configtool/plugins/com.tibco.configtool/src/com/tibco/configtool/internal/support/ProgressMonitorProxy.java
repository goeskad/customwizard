package com.tibco.configtool.internal.support;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.core.runtime.IProgressMonitor;

public class ProgressMonitorProxy extends Writer {
	private IProgressMonitor progressMonitor;

	public ProgressMonitorProxy(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public void write(String str) throws IOException {
		progressMonitor.subTask(str.replace(System.getProperty("line.separator"), ""));
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
	}
}
