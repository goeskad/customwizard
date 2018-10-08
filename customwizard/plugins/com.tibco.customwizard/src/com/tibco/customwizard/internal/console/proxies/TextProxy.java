package com.tibco.customwizard.internal.console.proxies;

import org.nuxeo.xforms.xforms.events.XFormsValueChangedEvent;

public class TextProxy extends EditableControlProxy {
	public void setXMLValue(String value) {
		this.value = (value == null ? "" : value); 
		if (requiresValidation()) {
			fireXFormsEvent(new XFormsValueChangedEvent(this));
		}
	}
}
