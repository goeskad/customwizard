package com.tibco.configtool.internal.support;

import java.io.File;
import java.io.PrintStream;

import com.tibco.configtool.support.IAsyncTaskListener;
import com.tibco.configtool.utils.TCTHelper;

public class LaunchStatusChecker implements IAsyncTaskListener{
	private String[] cmdarray;
	private File dir;
	private String returnValue;
	private int interval = 5000;
	
	private boolean taskDone;
	private Throwable error;
	
	public LaunchStatusChecker(String[] cmdarray, File dir, String returnValue, int interval) {
		this.cmdarray = cmdarray;
		this.dir = dir;
		this.returnValue = returnValue;
		this.interval = interval;
	}

	public boolean checkStatus() throws Throwable {
		int count = 0;
		while (!taskDone && error == null) {
			Thread.sleep(interval);
			System.out.println("start checking status");
			int returnCode = TCTHelper.execCommand(cmdarray, dir, (PrintStream) null, null);
			count++;
			System.out.println("return status: " + returnCode);
			if (String.valueOf(returnCode).equals(returnValue)) {
				return true;
			} else {
				if (count < 3) {
					Thread.sleep(interval);
				} else {
					throw new RuntimeException("Unable to launch the program, the return status is [" + returnCode
							+ "]");
				}
			}
		}

		if (error != null) {
			throw error;
		}

		return false;
	}
	
	public void taskDone() {
		taskDone = true;
	}

	public void taskError(Throwable error) {
		this.error = error;
	}

}
