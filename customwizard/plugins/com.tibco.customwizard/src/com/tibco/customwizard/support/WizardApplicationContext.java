package com.tibco.customwizard.support;

public class WizardApplicationContext {
	private static String[] appArgs;

	public static void setAppArgs(String[] appArgs) {
		WizardApplicationContext.appArgs = appArgs;
	}

	public static String[] getAppArgs() {
		return appArgs;
	}

	public static String getArgValue(String argName) {
		for (int i = 0; i < appArgs.length; i++) {
			if (appArgs[i].equals(argName)) {
				if (i + 1 < appArgs.length) {
					return appArgs[i + 1];
				}
				return "";
			}
		}
		return null;
	}

	public static boolean hasArg(String argName) {
		for (String arg : appArgs) {
			if (arg.equals(argName)) {
				return true;
			}
		}
		return false;
	}
}
