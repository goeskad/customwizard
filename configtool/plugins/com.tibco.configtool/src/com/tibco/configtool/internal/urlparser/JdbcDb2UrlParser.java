package com.tibco.configtool.internal.urlparser;

import static com.tibco.configtool.internal.urlparser.Constants.*;
import static com.tibco.configtool.utils.URLParser.*;

import java.util.Properties;
import java.util.regex.Matcher;

public class JdbcDb2UrlParser extends BaseUrlParser {

	public JdbcDb2UrlParser() {
		super("jdbc:db2:", DB2);
	}

	public Properties parse(String url) throws Exception {
		Matcher matcher = PATTERN_JDBC_DB2.matcher(url);
		if (matcher.matches()) {
			String host = matcher.group(1);
			String port = matcher.group(2);
			String dbname = matcher.group(3);
			String[][] propPairs = {{HOST, trimHost(host)}, {PORT, port}, {DATABASE, dbname}};
			return createProperties(propPairs);
		} else {
			throw new Exception(getMessageProvider().getMessage("URLParser.db2.error"));
		}
	}

}
