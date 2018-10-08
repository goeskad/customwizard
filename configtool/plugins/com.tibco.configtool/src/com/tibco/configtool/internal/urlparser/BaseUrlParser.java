package com.tibco.configtool.internal.urlparser;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.configtool.utils.URLParser;

abstract public class BaseUrlParser implements IUrlParser {

	private static final Pattern PATTERN_HOST_WITH_SQUARE_BRACKETS = Pattern.compile("^\\x5B(.*)\\x5D$");

	private String prefix;
	private String type;
	private IMessageProvider msgProvider;

	protected BaseUrlParser(String prefix, String type) {
		this.prefix = prefix;
		this.type = type;
		boolean DEBUG = Boolean.valueOf(System.getProperty("url.parser.debug"));
		if (DEBUG) {
			this.msgProvider = new DebugMessageProvider();
		} else {
			try {
				this.msgProvider = TCTHelper.getMessageProvider(
						TCTContext.getInstance().getWizardInstance());
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Initialize Message Provider faild.", e);
			}
		}
	}

	public boolean fastMatch(String url) {
		return url.startsWith(prefix);
	}

	protected final IMessageProvider getMessageProvider() {
		return msgProvider;
	}

	protected final String getType() {
		return type;
	}

	protected final Properties createProperties(String[][] propPairs) {
		Properties props = new Properties();
		props.setProperty(URLParser.TYPE, getType());
		for (String[] pair : propPairs) {
			props.setProperty(pair[0], pair[1]);
		}
		return props;
	}

	protected final String trimHost(String oriHost) {
		String newHost = oriHost;
		Matcher matcher = PATTERN_HOST_WITH_SQUARE_BRACKETS.matcher(oriHost);
		if (matcher.matches()) {
			newHost = matcher.group(1);
		}
		return newHost;
	}



	/**
	 * This class is only for DEBUG.
	 * @author huabin
	 *
	 */
	private static class DebugMessageProvider implements IMessageProvider{
		public String getMessage(String key) {
			return key;
		}
		public String getMessage(String key, Object... arguments) {
			return key;
		}
	}

}
