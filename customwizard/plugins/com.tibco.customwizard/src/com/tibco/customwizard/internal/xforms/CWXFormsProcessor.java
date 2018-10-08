package com.tibco.customwizard.internal.xforms;

import org.nuxeo.xforms.XFormsProcessor;
import org.nuxeo.xforms.stylesheet.StyleSheet;
import org.nuxeo.xforms.xml.XFactoryRegistry;

public class CWXFormsProcessor extends XFormsProcessor {
	public CWXFormsProcessor() {
		super();
		mFormLoader = new CWXFLoader(this);
	}

	@Override
	public StyleSheet loadDefaultStyleSheet() {
		// return an empty style sheet as default
		return new StyleSheet();
	}

	public void setFactoryRegistry(XFactoryRegistry factoryRegistry) {
		((CWXFLoader) mFormLoader).setFactoryRegistry(factoryRegistry);
	}
}
