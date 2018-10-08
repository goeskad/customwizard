package com.tibco.customwizard.support;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class SystemConsoleProxy {
	private static final Console console;

	private static final BufferedReader reader;

	private static final PrintStream sysout;

	private static PrintStream consoleLog;

	static {
		console = System.console();
		reader = new BufferedReader(new InputStreamReader(System.in));

		sysout = System.out;
	}

	public static void setConsoleLog(PrintStream log) {
		consoleLog = log;
	}
	
	public static String readLine() {
		String input;
		if (console == null) {
			try {
				input = reader.readLine();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			input = console.readLine();
		}
		if (consoleLog != null)
			consoleLog.println(input);
		return input;
	}

	public static String readPassword() {
		String input;
		if (console == null) {
			try {
				input = reader.readLine();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			input = new String(console.readPassword());
		}
		if (consoleLog != null)
			consoleLog.println(input);
		return input;
	}

	public static void print(String msg) {
		sysout.print(msg);
		if (consoleLog != null)
			consoleLog.print(msg);
	}

	public static void println(String msg) {
		sysout.println(msg);
		if (consoleLog != null)
			consoleLog.println(msg);
	}
}
