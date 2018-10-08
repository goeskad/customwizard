package com.tibco.customwizard.support;

import com.tibco.customwizard.config.WizardConfig;

public interface IWizardConfigLocator {
	public WizardConfig findWizardConfig(String id) throws Exception;
}
