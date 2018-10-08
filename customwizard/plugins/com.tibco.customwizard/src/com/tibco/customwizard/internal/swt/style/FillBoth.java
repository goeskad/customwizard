package com.tibco.customwizard.internal.swt.style;

import org.nuxeo.xforms.ui.swt.BuildContext;
import org.nuxeo.xforms.ui.swt.StyleProperty;
import org.nuxeo.xforms.ui.swt.TypeConverter;

public class FillBoth extends StyleProperty {
	public boolean process(BuildContext context, String value) {
		boolean b = TypeConverter.booleanValue(value);
		if (b && context.layoutData != null) {
			context.layoutDataProxy.setAlign(context, "fill");
			context.layoutDataProxy.setVAlign(context, "fill");
			context.layoutDataProxy.setHGrab(context, value);
			context.layoutDataProxy.setVGrab(context, value);
		}
		return true;
	}
}
