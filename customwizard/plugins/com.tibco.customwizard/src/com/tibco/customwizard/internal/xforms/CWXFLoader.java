package com.tibco.customwizard.internal.xforms;

import org.nuxeo.xforms.XFormsProcessor;
import org.nuxeo.xforms.xforms.XFLoader;
import org.nuxeo.xforms.xml.XFactoryRegistry;

public class CWXFLoader extends XFLoader {
	private XFactoryRegistry factoryRegistry;

	public CWXFLoader(XFormsProcessor processor) {
		super(processor);
	}

	public void setFactoryRegistry(XFactoryRegistry factoryRegistry) {
		this.factoryRegistry = factoryRegistry;
	}

	public XFactoryRegistry getFactoryRegistry(String namespace) {
		if (factoryRegistry == null) {
			return super.getFactoryRegistry(namespace);
		}
		return factoryRegistry;
	}
}
