package com.tibco.customwizard.xforms;

import org.nuxeo.xforms.XFormsProcessor;
import org.nuxeo.xforms.ui.UIBuilder;

public interface IXFormExtension {
	XFormsProcessor getXFormsProcessor();

	UIBuilder getUIBuilder();
}
