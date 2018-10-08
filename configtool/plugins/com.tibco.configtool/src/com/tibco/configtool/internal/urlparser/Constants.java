package com.tibco.configtool.internal.urlparser;

import java.util.regex.Pattern;

public class Constants {

	/**
	 **********************************
	 * Simple URL Regular Expression *
	 **********************************
	 */
	public static final String REGEX_PROTOCOL_SIMPLE = "[a-z]+";

	public static final String REGEX_IPv4 = "(?:(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))";
	public static final String REGEX_IPv6 = "(?:(?:(?:(?:[0-9A-Fa-f]{1,4}:){7}(?:[0-9A-Fa-f]{1,4}|:))|(?:(?:[0-9A-Fa-f]{1,4}:){6}(?::[0-9A-Fa-f]{1,4}|(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(?:(?:[0-9A-Fa-f]{1,4}:){5}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,2})|:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(?:(?:[0-9A-Fa-f]{1,4}:){4}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,3})|(?:(?::[0-9A-Fa-f]{1,4})?:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(?:(?:[0-9A-Fa-f]{1,4}:){3}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,4})|(?:(?::[0-9A-Fa-f]{1,4}){0,2}:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(?:(?:[0-9A-Fa-f]{1,4}:){2}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,5})|(?:(?::[0-9A-Fa-f]{1,4}){0,3}:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(?:(?:[0-9A-Fa-f]{1,4}:){1}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,6})|(?:(?::[0-9A-Fa-f]{1,4}){0,4}:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(?::(?:(?:(?::[0-9A-Fa-f]{1,4}){1,7})|(?:(?::[0-9A-Fa-f]{1,4}){0,5}:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(?:%.+)?)";
//	public static final String REGEX_HOST = "(?:(?:(?:(?:[\\w&&[^_]])+(?:[\\w|-])*(?:[\\w]?[.]))*)(?:(?:[A-Z|a-z])+(?:[\\w|-])*(?:[\\w]?)))";
	public static final String REGEX_HOST = "(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)*[a-zA-Z0-9](?:[a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?)";
	public static final String REGEX_HOST_OR_IPv4 = REGEX_HOST + "|" + REGEX_IPv4;
	public static final String REGEX_HOST_OR_IPv4_OR_IPv6 = REGEX_HOST + "|" + REGEX_IPv4 + "|" + REGEX_IPv6;
	public static final String REGEX_HOST_OR_IPv4_OR_IPv6_WITH_SQUARE_BRACKETS = REGEX_HOST + "|" + REGEX_IPv4 + "|" + "(?:\\x5B" + REGEX_IPv6 + "\\x5D)";
	public static final String REGEX_PORT = "(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)"; // Capturing group


	public static final String REGEX_SIMPLE = "^" + REGEX_PROTOCOL_SIMPLE + "://" +
														"(" + REGEX_HOST_OR_IPv4_OR_IPv6_WITH_SQUARE_BRACKETS + "):" + // Capturing group
														REGEX_PORT + "(?:/.*)*/*$";


	/**
	 *******************************
	 * JDBC URL Regular Expression *
	 *******************************
	 */
	public static final String REGEX_PROTOCOL_JDBC = "jdbc";

	public static final String REGEX_JDBC_PROVIDOR_ORACLE = "oracle";
	public static final String REGEX_JDBC_PROVIDOR_MSSQL = "sqlserver";
	public static final String REGEX_JDBC_PROVIDOR_HSQLDB = "hsqldb";
	public static final String REGEX_JDBC_PROVIDOR_DB2 = "db2";

	public static final String REGEX_JDBC_SUB_PROTOCOL_ORACLE_THIN = "thin";
	public static final String REGEX_JDBC_SUB_PROTOCOL_HSQLDB_HSQL = "hsql";

	public static final String REGEX_DB_SINGLE_PROPERTY_1 = "[a-zA-Z]+\\w*=\\w+";
	public static final String REGEX_DB_SINGLE_PROPERTY_WITH_SEMICOLON_1 = "(?:" + REGEX_DB_SINGLE_PROPERTY_1 + ")?;?";
	public static final String REGEX_DB_MULTI_PROPERTY_WITH_SEMICOLON_1 = "(?:" + REGEX_DB_SINGLE_PROPERTY_1 + ";)*";

	public static final String REGEX_DB_INSTANCE_NAME_1 = "([a-zA-Z][\\w|#|$]*)"; // Capturing group
	public static final String REGEX_DB_INSTANCE_NAME_2 = "((?:\\([a-zA-Z]\\w*=.+\\)){3,})"; // Capturing group
	public static final String REGEX_DB_INSTANCE_NAME_3 = REGEX_DB_MULTI_PROPERTY_WITH_SEMICOLON_1 +
														"(?i:DatabaseName)=([a-zA-Z]+\\w*);*" + // Capturing group
														REGEX_DB_MULTI_PROPERTY_WITH_SEMICOLON_1 +
														REGEX_DB_SINGLE_PROPERTY_WITH_SEMICOLON_1;
	public static final String REGEX_DB_INSTANCE_NAME_4 = "(?:" + REGEX_DB_MULTI_PROPERTY_WITH_SEMICOLON_1 +
														"(?i:ServerName)=(" + REGEX_IPv6 + ");" + // Capturing group
														REGEX_DB_MULTI_PROPERTY_WITH_SEMICOLON_1 +
														"(?i:DatabaseName)=([a-zA-Z]+\\w*);?" + // Capturing group
														REGEX_DB_MULTI_PROPERTY_WITH_SEMICOLON_1 +
														REGEX_DB_SINGLE_PROPERTY_WITH_SEMICOLON_1 + ")";
	public static final String REGEX_DB_INSTANCE_NAME_5 = "(?:" + REGEX_DB_MULTI_PROPERTY_WITH_SEMICOLON_1 +
														"(?i:DatabaseName)=([a-zA-Z]+\\w*);" + // Capturing group
														REGEX_DB_MULTI_PROPERTY_WITH_SEMICOLON_1 +
														"(?i:ServerName)=(" + REGEX_IPv6 + ");?" + // Capturing group
														REGEX_DB_MULTI_PROPERTY_WITH_SEMICOLON_1 +
														REGEX_DB_SINGLE_PROPERTY_WITH_SEMICOLON_1 + ")";

	public static final String REGEX_JDBC_ORACLE_HOST = "(?:.*\\(HOST=(" + REGEX_HOST_OR_IPv4_OR_IPv6 + ")\\).*)"; // Capturing group
	public static final String REGEX_JDBC_ORACLE_PORT = "(?:.*\\(PORT=" + REGEX_PORT + "\\).*)"; // Capturing group
	public static final String REGEX_JDBC_ORACLE_SID = "(?:(?:.*\\(SID=" + REGEX_DB_INSTANCE_NAME_1 + "\\).*)|(?:.*\\(SERVICE_NAME=" + REGEX_DB_INSTANCE_NAME_1 + "\\).*))"; // Capturing group

	public static final String REGEX_JDBC_ORACLE_THIN_1 = "^" + REGEX_PROTOCOL_JDBC + ":" +
														REGEX_JDBC_PROVIDOR_ORACLE + ":" +
														REGEX_JDBC_SUB_PROTOCOL_ORACLE_THIN + ":@" +
														"(" + REGEX_HOST_OR_IPv4_OR_IPv6_WITH_SQUARE_BRACKETS + "):" + // Capturing group
														REGEX_PORT + "[:|/]" +
														REGEX_DB_INSTANCE_NAME_1 + "$";
	public static final String REGEX_JDBC_ORACLE_THIN_2 = "^(" + REGEX_PROTOCOL_JDBC + ":" + // Capturing group
														REGEX_JDBC_PROVIDOR_ORACLE + ":" +
														REGEX_JDBC_SUB_PROTOCOL_ORACLE_THIN + ":@)" +
														REGEX_DB_INSTANCE_NAME_2 + "$";


	public static final String REGEX_JDBC_HSQLDB_HSQL = "^" + REGEX_PROTOCOL_JDBC + ":" +
														REGEX_JDBC_PROVIDOR_HSQLDB + ":" +
														REGEX_JDBC_SUB_PROTOCOL_HSQLDB_HSQL + "://" +
														"(" + REGEX_HOST_OR_IPv4_OR_IPv6_WITH_SQUARE_BRACKETS + "):" + // Capturing group
														REGEX_PORT + "/" +
														REGEX_DB_INSTANCE_NAME_1 + "$";

	public static final String REGEX_JDBC_MSSQL_1 = "^" + REGEX_PROTOCOL_JDBC + ":" +
														REGEX_JDBC_PROVIDOR_MSSQL + "://" +
														"(" + REGEX_HOST_OR_IPv4 + "):" + // Capturing group
														REGEX_PORT + ";" +
														REGEX_DB_INSTANCE_NAME_3 + "$";
	public static final String REGEX_JDBC_MSSQL_2 = "^" + REGEX_PROTOCOL_JDBC + ":" +
														REGEX_JDBC_PROVIDOR_MSSQL + "://" +
														REGEX_PORT + ";" +
														REGEX_DB_INSTANCE_NAME_4 + "$";
	public static final String REGEX_JDBC_MSSQL_3 = "^" + REGEX_PROTOCOL_JDBC + ":" +
														REGEX_JDBC_PROVIDOR_MSSQL + "://" +
														REGEX_PORT + ";" +
														REGEX_DB_INSTANCE_NAME_5 + "$";

	public static final String REGEX_JDBC_DB2 = "^" + REGEX_PROTOCOL_JDBC + ":" +
														REGEX_JDBC_PROVIDOR_DB2 + "://" +
														"(" + REGEX_HOST_OR_IPv4_OR_IPv6_WITH_SQUARE_BRACKETS + "):" + // Capturing group
														REGEX_PORT + "/" +
														REGEX_DB_INSTANCE_NAME_1 + "$";



	public static final Pattern PATTERN_JDBC_ORACLE_THIN_1 = Pattern.compile(REGEX_JDBC_ORACLE_THIN_1);
	public static final Pattern PATTERN_JDBC_ORACLE_THIN_2 = Pattern.compile(REGEX_JDBC_ORACLE_THIN_2);
	public static final Pattern PATTERN_JDBC_ORACLE_HOST = Pattern.compile(REGEX_JDBC_ORACLE_HOST);
	public static final Pattern PATTERN_JDBC_ORACLE_PORT = Pattern.compile(REGEX_JDBC_ORACLE_PORT);
	public static final Pattern PATTERN_JDBC_ORACLE_SID = Pattern.compile(REGEX_JDBC_ORACLE_SID);

	public static final Pattern PATTERN_JDBC_MSSQL_1 = Pattern.compile(REGEX_JDBC_MSSQL_1);
	public static final Pattern PATTERN_JDBC_MSSQL_2 = Pattern.compile(REGEX_JDBC_MSSQL_2);
	public static final Pattern PATTERN_JDBC_MSSQL_3 = Pattern.compile(REGEX_JDBC_MSSQL_3);

	public static final Pattern PATTERN_JDBC_HSQL = Pattern.compile(REGEX_JDBC_HSQLDB_HSQL);

	public static final Pattern PATTERN_JDBC_DB2 = Pattern.compile(REGEX_JDBC_DB2);

	public static final Pattern PATTERN_SIMPLE = Pattern.compile(REGEX_SIMPLE);


}
