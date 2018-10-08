package com.tibco.customwizard.internal.xml;

import org.nuxeo.xforms.xml.XFactoryRegistry;
import org.nuxeo.xforms.xml.XParserConfiguration;

import com.tibco.customwizard.config.WizardConfig;

public class WizardConfigParserConfiguration extends XParserConfiguration {
	private WizardConfig wizardConfig;

	public WizardConfigParserConfiguration(WizardConfig wizardConfig) {
		this.wizardConfig = wizardConfig;
	}

	public Object createDocument() {
		return wizardConfig;
	}

	public XFactoryRegistry getFactoryRegistry(String uri) {
		return WizardConfigFactoryRegistry.getInstance();
	}
}
