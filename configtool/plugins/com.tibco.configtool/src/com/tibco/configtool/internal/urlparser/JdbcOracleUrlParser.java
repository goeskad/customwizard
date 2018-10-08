package com.tibco.configtool.internal.urlparser;

import static com.tibco.configtool.internal.urlparser.Constants.PATTERN_JDBC_ORACLE_HOST;
import static com.tibco.configtool.internal.urlparser.Constants.PATTERN_JDBC_ORACLE_PORT;
import static com.tibco.configtool.internal.urlparser.Constants.PATTERN_JDBC_ORACLE_SID;
import static com.tibco.configtool.internal.urlparser.Constants.PATTERN_JDBC_ORACLE_THIN_1;
import static com.tibco.configtool.internal.urlparser.Constants.PATTERN_JDBC_ORACLE_THIN_2;
import static com.tibco.configtool.utils.URLParser.DATABASE;
import static com.tibco.configtool.utils.URLParser.HOST;
import static com.tibco.configtool.utils.URLParser.ORACLE;
import static com.tibco.configtool.utils.URLParser.PORT;

import java.util.Properties;
import java.util.regex.Matcher;

public class JdbcOracleUrlParser extends BaseUrlParser {

	public JdbcOracleUrlParser() {
		super("jdbc:oracle:", ORACLE);
	}

	public Properties parse(String url) throws Exception {
		String host = "";
		String port = "";
		String dbname = "";
		if (url.startsWith("jdbc:oracle:thin:@(")) {
			Matcher matcher = PATTERN_JDBC_ORACLE_THIN_2.matcher(url);
			if (matcher.matches()) {
				String args = matcher.group(2);
				Matcher argMatcher = PATTERN_JDBC_ORACLE_HOST.matcher(args);
				if (argMatcher.matches()) {
					host = argMatcher.group(1);
				} else {
					throw new Exception(getMessageProvider().getMessage("URLParser.oracle.error.host"));
				}
				argMatcher = PATTERN_JDBC_ORACLE_PORT.matcher(args);
				if (argMatcher.matches()) {
					port = argMatcher.group(1);
				} else {
					throw new Exception(getMessageProvider().getMessage("URLParser.oracle.error.port"));
				}
				argMatcher = PATTERN_JDBC_ORACLE_SID.matcher(args);
				if (argMatcher.matches()) {
					dbname = argMatcher.group(1);
				} else {
					throw new Exception(getMessageProvider().getMessage("URLParser.oracle.error.sid"));
				}
			} else {
				throw new Exception(getMessageProvider().getMessage("URLParser.oracle.error"));
			}
		} else {
			String errorMsg = getMessageProvider().getMessage("URLParser.oracle.error");
			Matcher matcher = PATTERN_JDBC_ORACLE_THIN_1.matcher(url);
			if (matcher.matches()) {
				host = matcher.group(1);
				port = matcher.group(2);
				dbname = matcher.group(3);
			} else {
				throw new Exception(errorMsg);
			}
		}
		String[][] propPairs = {{HOST, trimHost(host)}, {PORT, port}, {DATABASE, dbname}};
		return createProperties(propPairs);
	}

}
