package com.tibco.customwizard.internal.swt.proxies;

public class ComboProxy extends org.nuxeo.xforms.ui.swt.proxies.ComboProxy {
	public void setXMLValue(String value) {
		if (value == null) {
			return;
		}
		super.setXMLValue(value);
	}
}
