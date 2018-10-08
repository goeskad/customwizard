package com.tibco.customwizard.internal.console.proxies;

import org.nuxeo.xforms.xforms.events.ClickEvent;

import com.tibco.customwizard.support.SystemConsoleProxy;

public class CheckBoxProxy extends EditableControlProxy {
	protected void printDefaultValue() {
		SystemConsoleProxy.print("[" + (Boolean.parseBoolean(value) ? "yes" : "no") + "]");
	}
	
	protected void processInput(String input) {
		value = Boolean.toString((input != null) && input.equalsIgnoreCase("yes"));
		if (requiresValidation()) {
			fireXFormsEvent(new ClickEvent(this, null));
		}
	}
}
