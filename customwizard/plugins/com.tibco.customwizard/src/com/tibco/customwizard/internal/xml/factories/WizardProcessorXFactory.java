package com.tibco.customwizard.internal.xml.factories;

import org.xml.sax.Attributes;

import com.tibco.customwizard.config.IWizardProcessor;
import com.tibco.customwizard.config.WizardConfig;

public class WizardProcessorXFactory extends AbstracteWizardConfigXFactory {
	public final static WizardProcessorXFactory INSTANCE = new WizardProcessorXFactory();

	protected Object create(WizardConfig wizardConfig, String name,
			Object parent, Attributes attrs) throws Exception {
		IWizardProcessor wizardProcessor = (IWizardProcessor) createObject(
				wizardConfig, attrs);
		wizardConfig.setWizardProcessor(wizardProcessor);
		return wizardProcessor;
	}
}
