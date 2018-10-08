package com.tibco.customwizard.internal.xml.factories;

import org.xml.sax.Attributes;

import com.tibco.customwizard.config.WizardConfig;

public class WizardXFactory extends AbstracteWizardConfigXFactory {
	public final static WizardXFactory INSTANCE = new WizardXFactory();

	protected Object create(WizardConfig wizardConfig, String name,
			Object parent, Attributes attrs) throws Exception {
		return wizardConfig;
	}
}