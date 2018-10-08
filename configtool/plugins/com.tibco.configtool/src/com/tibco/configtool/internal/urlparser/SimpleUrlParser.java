package com.tibco.configtool.internal.urlparser;

import static com.tibco.configtool.internal.urlparser.Constants.*;
import static com.tibco.configtool.utils.URLParser.*;

import java.util.Properties;
import java.util.regex.Matcher;

public class SimpleUrlParser extends BaseUrlParser {

	public SimpleUrlParser(String prefix, String type) {
		super(prefix, type);
	}

	public Properties parse(String url) throws Exception {
		Matcher matcher = PATTERN_SIMPLE.matcher(url);
		if (matcher.matches()) {
			String host = matcher.group(1);
			String port = matcher.group(2);
			String[][] propPairs = {{HOST, trimHost(host)}, {PORT, port}};
			return createProperties(propPairs);
		} else {
			throw new Exception(getMessageProvider().getMessage("URLParser.url.error", getType()));
		}
	}

}
