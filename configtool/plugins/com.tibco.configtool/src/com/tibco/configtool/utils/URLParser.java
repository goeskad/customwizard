package com.tibco.configtool.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.tibco.configtool.internal.urlparser.IUrlParser;
import com.tibco.configtool.internal.urlparser.JdbcDb2UrlParser;
import com.tibco.configtool.internal.urlparser.JdbcHsqlUrlParser;
import com.tibco.configtool.internal.urlparser.JdbcMsSqlUrlParser;
import com.tibco.configtool.internal.urlparser.JdbcOracleUrlParser;
import com.tibco.configtool.internal.urlparser.SimpleUrlParser;

public class URLParser {
	public static final String TYPE = "type";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String DATABASE = "database";

	public static final String ORACLE = "oracle";
	public static final String MSSQL = "mssql";
	public static final String HSQL = "hsql";
	public static final String MYSQL = "mysql";
	public static final String DB2 = "db2";

	private static final List<? extends IUrlParser> PARSERS = Arrays.asList(
			new JdbcOracleUrlParser(),
			new JdbcMsSqlUrlParser(),
			new JdbcHsqlUrlParser(),
			new JdbcDb2UrlParser(),
			new SimpleUrlParser("ldap:", "ldap"),
			new SimpleUrlParser("ldaps:", "ldaps"),
			new SimpleUrlParser("tcp:", "tcp"),
			new SimpleUrlParser("ssl:", "ssl"));

	public static Properties parse(String url) throws Exception {
		for (IUrlParser parser : PARSERS) {
			if (parser.fastMatch(url)) {
				return parser.parse(url);
			}
		}
		throw new Exception("Unknown format");
	}

//	public static void main(String[] args) throws Exception {
//		String[] urls = {
//				// oracle correct
//				"jdbc:oracle:thin:@huabin-lt:1521:ORCL#123",
//				"jdbc:oracle:thin:@[1111:1111:1111:1111:1111:1111:FFFF:F1E2]:1521:ORCL#123",
//				"jdbc:oracle:thin:@[fe80::1]:1521:ORCL#123",
//				"jdbc:oracle:thin:@huabin-lt:1521/ORCL",
//				"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCPS)(HOST=fe80::1)(PORT=1521)))(CONNECT_DATA=(SID=abcd)(SERVER=dedicated)))",
//				"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCPS)(HOST=::13.1.68.3)(PORT=1521)))(CONNECT_DATA=(SID=you)(SERVER=dedicated)))",
//				// oracle incorrect
//				"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCPS)(HOST=fe80::1)(PORT=666666)))(CONNECT_DATA=(SID=uuu)(SERVER=dedicated)))",
//				"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCPS)(HOST=fe80::1)(PORT=666666)))(CONNECT_DATA=(SID=ddd)(SERVER=dedicated)))",
//				"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCPS)(HOST=::1111:2222:3333:4444:5555:6666::)(PORT=1521)))(CONNECT_DATA=(SID=1234)(SERVER=dedicated)))",
//				"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCPS)(HOST=::300.300.300.300)(PORT=1521)))(CONNECT_DATA=(SID=1234)(SERVER=dedicated)))",
//				"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCPS)(PORT=1521)))(CONNECT_DATA=(SID=1234)(SERVER=dedicated)))",
//				// mssql correct
//				"jdbc:sqlserver://192.168.1.1:1433;DatabaseName=haha;a=1;b=3;c=2;",
//				"jdbc:sqlserver://haha.com:1433;DatabaseName=haha;a=1;b=3;c=2;",
//				"jdbc:sqlserver://126.tibco.com:1433;DatabaseName=haha;a=1;b=3;c=2;",
//				"jdbc:sqlserver://1433;ServerName=1111:1111:1111:1111:1111:1111:FFFF:F1E2;DatabaseName=xyz;a=1;b=3;c=2;",
//				"jdbc:sqlserver://1433;DatabaseName=xyz;ServerName=2001:0db8:1234::;a=1;b=3;c=2;",
//				"jdbc:sqlserver://1433;c=2;ServerName=1111:1111:1111:1111:1111:1111:FFFF:F1E2;c=2;DatabaseName=xyz;a=1;b=3;c=2;",
//				"jdbc:sqlserver://1433;b=3;DATABASENAME=xyz;b=3;SERVERNAME=::1;a=1;b=3;c=2;",
//				"jdbc:sqlserver://1433;c=2;ServErName=::7777:8888;c=2;databaseName=xyz",
//				"jdbc:sqlserver://1433;b=3;datABaseName=xyz;b=3;serverName=::123.123.123.123",
//				// mssql incorrect
//				"jdbc:sqlserver://1433;b=3;DatABaseNaME=xyz;b=3;",
//				"jdbc:sqlserver://1433;ServerName=haha.com;DatabaseName=xyz;a=1;b=3;c=2;",
//				"jdbc:sqlserver://fe80::1:1433;databasename=haha;a=1;b=3;c=2;",
//				"jdbc:sqlserver://1:2:3:4::5:1.2.3.4:1433;DatABaseName=helloMySql;a=1;b=3;c=2;",
//				// hsql correct
//				"jdbc:hsqldb:hsql://[fe80::1]:1234/amx",
//				"jdbc:hsqldb:hsql://192.168.11.11:5678/amx",
//				"jdbc:hsqldb:hsql://haha.com:5678/amx",
//				"jdbc:hsqldb:hsql://[::123.123.123.123]:777/amx",
//				// hsql incorrect
//				"jdbc:hsqldb:hsql://[fe80::1:1234/amx",
//				"jdbc:hsqldb:hsql://fe80::1:1234",
//				"jdbc:hsqldb:hsql://192.168.11.11:910JQ/amx",
//				"jdbc:hsqldb:hsql://2828/amx",
//				// db2 correct
//				"jdbc:db2://[fe80::1]:1234/amx",
//				"jdbc:db2://[::123.123.123.123]:1234/amx",
//				"jdbc:db2://[2001:0000:1234:0000:0000:C1C0:ABCD:0876]:1234/amx",
//				// db2 incorrect
//				"jdbc:db2://1234/amx",
//				// generic correct
//				"ldap://192.168.1.12:80/h/e/l/l/o",
//				"ldap://[2001:0000:1234:0000:0000:C1C0:ABCD:0876]:80/h/e/l/l/o",
//				"ldaps://[2001:0000:1234:0000:0000:C1C0:ABCD:0876]:80/h/e/l/l/o",
//				"ssl://[2001:0000:1234:0000:0000:C1C0:ABCD:0876]:80/h/e/l/l/o",
//				"tcp://[2001:0000:1234:0000:0000:C1C0:ABCD:0876]:80/h/e/l/l/o",
//				// generic incorrect
//				"ldap://okok:88888",
//				// unknown
//				"haha://2001:0000:1234:0000:0000:C1C0:ABCD:0876:80/h/e/l/l/o"};
//		for (String url : urls) {
//			try {
//				System.out.println(parse(url));
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//		}
//	}

}
