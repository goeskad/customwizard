package com.tibco.customwizard.internal.swt.style;

import org.nuxeo.xforms.ui.swt.BuildContext;
import org.nuxeo.xforms.ui.swt.StyleProperty;

public class Disable extends StyleProperty {
	public boolean process(BuildContext descriptor, String value) {
		if (descriptor.proxy == null) {
			return false;
		}
		descriptor.proxy.setEnabled(!Boolean.parseBoolean(value));
		return true;
	}
}
