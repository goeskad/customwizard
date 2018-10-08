package com.tibco.customwizard.internal.console.proxies;

import java.util.List;

import org.nuxeo.xforms.xforms.events.ClickEvent;

import com.tibco.customwizard.support.SystemConsoleProxy;

public class RadioProxy extends EditableControlProxy {
	protected void printDefaultValue() {
		SystemConsoleProxy.print("[" + (Boolean.parseBoolean(value) ? "yes" : "no") + "]");
	}
	
	protected void processInput(String input) {
		value = Boolean.toString((input != null) && input.equalsIgnoreCase("yes"));
		
		if (value.equals("true")) {
			// unselect the reset radio
			List<ConsoleControlProxy> controlList = parent.getControlList();
			for (ConsoleControlProxy control : controlList) {
				if (control != this && control instanceof RadioProxy) {
					control.setXMLValue("false");
				}
			}
		}
		
		if (requiresValidation()) {
			fireXFormsEvent(new ClickEvent(this, null));
		}
	}
}
