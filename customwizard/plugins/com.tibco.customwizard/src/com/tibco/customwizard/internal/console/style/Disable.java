package com.tibco.customwizard.internal.console.style;

import com.tibco.customwizard.internal.console.BuildContext;
import com.tibco.customwizard.internal.console.StyleProperty;

public class Disable implements StyleProperty {
	public boolean process(BuildContext descriptor, String value) {
		if (descriptor.proxy == null) {
			return false;
		}
		descriptor.proxy.setEnabled(!Boolean.parseBoolean(value));
		return true;
	}
}
