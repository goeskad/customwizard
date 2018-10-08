package com.tibco.configtool.internal.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class DBConnectionTester {
	public static void testDBConnection(String driver, String url, Properties info, boolean createTable)
			throws Exception {
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, info);

		try {
			if (createTable) {
				Statement stmt = conn.createStatement();
				try {
					stmt.executeUpdate("create table tct_test (tct_id char(1))");
					stmt.executeUpdate("drop table tct_test");
				} finally {
					stmt.close();
				}
			}
		} finally {
			conn.close();
		}
	}
}
