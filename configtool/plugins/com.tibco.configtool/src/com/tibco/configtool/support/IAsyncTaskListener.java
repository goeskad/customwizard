package com.tibco.configtool.support;

public interface IAsyncTaskListener {
	void taskDone();
	void taskError(Throwable error);
}
