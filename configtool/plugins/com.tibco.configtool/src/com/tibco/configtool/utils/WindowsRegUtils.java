package com.tibco.configtool.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class WindowsRegUtils {
	private static final String REGQUERY_UTIL = "reg query ";
	private static final String REGSTR_TOKEN = "REG_SZ";
	private static final String REGDWORD_TOKEN = "REG_DWORD";

	public static String getStringValue(String subKey, String valueKey) throws Exception {
		String value = queryRegValue(subKey, valueKey);

		int p = value.indexOf(REGSTR_TOKEN);

		if (p == -1)
			return null;

		return value.substring(p + REGSTR_TOKEN.length()).trim();
	}

	public static String getDWordValue(String subKey, String valueKey) throws Exception {
		String value = queryRegValue(subKey, valueKey);

		int p = value.indexOf(REGDWORD_TOKEN);

		if (p == -1)
			return null;

		String temp = value.substring(p + REGDWORD_TOKEN.length()).trim();
		return Integer.toString(Integer.parseInt(temp.substring("0x".length()), 16));
	}

	private static String queryRegValue(String subKey, String valueKey) throws Exception {
		Process process = Runtime.getRuntime().exec(REGQUERY_UTIL + "\"" + subKey + "\"" + " /v " + valueKey);
		StreamReader reader = new StreamReader(process.getInputStream());

		reader.start();
		process.waitFor();
		reader.join();

		return reader.getResult();
	}

	static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter sw;

		StreamReader(InputStream is) {
			this.is = is;
			sw = new StringWriter();
		}

		public void run() {
			try {
				int c;
				while ((c = is.read()) != -1)
					sw.write(c);
			} catch (IOException e) {
				;
			}
		}

		String getResult() {
			return sw.toString();
		}
	}
}
