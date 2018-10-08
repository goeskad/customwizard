package com.tibco.configtool.internal.support;

import java.io.File;

import com.tibco.configtool.support.IAsyncTaskListener;
import com.tibco.configtool.utils.TCTHelper;

public class AsyncCommandExecutor extends Thread {
	private IAsyncTaskListener listener;
	private String[] cmdarray;
	private File dir;
	private File logFile;
	
	public AsyncCommandExecutor(String[] cmdarray, File dir, File logFile, IAsyncTaskListener listener) {
		this.cmdarray = cmdarray;
		this.logFile = logFile;
		this.dir = dir;
		this.listener = listener;
	}

	public void run() {
		try {
			TCTHelper.execCommand(cmdarray, dir, logFile, null);
			listener.taskDone();
		} catch (Throwable e) {
			if (listener != null) {
				listener.taskError(e);
			}
		}
	}
}
