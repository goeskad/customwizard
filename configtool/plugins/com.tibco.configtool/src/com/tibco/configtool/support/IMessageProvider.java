package com.tibco.configtool.support;

public interface IMessageProvider {
	public String getMessage(String key);

	public String getMessage(String key, Object... arguments);
}
