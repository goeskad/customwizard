package com.tibco.customwizard.internal.console.style;

import com.tibco.customwizard.internal.console.BuildContext;
import com.tibco.customwizard.internal.console.StyleProperty;
import com.tibco.customwizard.internal.console.proxies.EditableControlProxy;

public class ReadOnly implements StyleProperty {
	public boolean process(BuildContext descriptor, String value) {
		if (descriptor.proxy == null) {
			return false;
		}

		if (descriptor.proxy instanceof EditableControlProxy) {
			((EditableControlProxy) descriptor.proxy).setEditable(!Boolean.parseBoolean(value));
		}
		return true;
	}
}