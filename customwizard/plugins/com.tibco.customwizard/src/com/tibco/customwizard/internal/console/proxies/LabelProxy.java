package com.tibco.customwizard.internal.console.proxies;

import com.tibco.customwizard.support.SystemConsoleProxy;

public class LabelProxy extends ConsoleControlProxy {
	public String getXMLValue() {
		return label;
	}

	public void setXMLValue(String value) {
		this.label = value;
	}
	
	public void present() {
		SystemConsoleProxy.println("\n" + label);
	}
}
