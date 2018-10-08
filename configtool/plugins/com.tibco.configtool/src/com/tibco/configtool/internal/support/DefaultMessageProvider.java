package com.tibco.configtool.internal.support;

import java.text.MessageFormat;
import java.util.Properties;

import com.tibco.configtool.support.IMessageProvider;

public class DefaultMessageProvider implements IMessageProvider {
	private IMessageProvider parent;

	private Properties props;

	public DefaultMessageProvider(IMessageProvider parent, Properties props) {
		this.parent = parent;
		this.props = props;
	}

	public String getMessage(String key, Object... arguments) {
		String message = getMessage(key);
		if (message != null) {
			message = MessageFormat.format(message, arguments);
		}
		return message;
	}

	public String getMessage(String key) {
		String message = props.getProperty(key);
		if (message == null && parent != null) {
			message = parent.getMessage(key);
		}
		return message;
	}
}
