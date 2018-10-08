package com.tibco.configtool.internal.urlparser;

import static com.tibco.configtool.internal.urlparser.Constants.*;
import static com.tibco.configtool.utils.URLParser.*;

import java.util.Properties;
import java.util.regex.Matcher;

public class JdbcMsSqlUrlParser extends BaseUrlParser {

	public JdbcMsSqlUrlParser() {
		super("jdbc:sqlserver:", MSSQL);
	}

	public Properties parse(String url) throws Exception {
		String host = "";
		String port = "";
		String dbname = "";
		Matcher matcher;
		if ((matcher = PATTERN_JDBC_MSSQL_1.matcher(url)).matches()) {
			host = matcher.group(1);
			port = matcher.group(2);
			dbname = matcher.group(3);
		} else if ((matcher = PATTERN_JDBC_MSSQL_2.matcher(url)).matches()) {
			port = matcher.group(1);
			host = matcher.group(2);
			dbname = matcher.group(3);
		} else if ((matcher = PATTERN_JDBC_MSSQL_3.matcher(url)).matches()) {
			port = matcher.group(1);
			dbname = matcher.group(2);
			host = matcher.group(3);
		} else {
			throw new Exception(getMessageProvider().getMessage("URLParser.mssql.error"));
		}
		String[][] propPairs = {{HOST, trimHost(host)}, {PORT, port}, {DATABASE, dbname}};
		return createProperties(propPairs);
	}

}
