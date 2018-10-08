package com.tibco.customwizard.internal.support;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.WizardInstance;

public class BaseActionContext implements IActionContext {
	private WizardInstance wizardInstance;
	
	public BaseActionContext(WizardInstance wizardInstance) {
		this.wizardInstance = wizardInstance;
	}

	public WizardConfig getWizardConfig() {
		return wizardInstance.getWizardConfig();
	}
	
	public WizardInstance getWizardInstance() {
		return wizardInstance;
	}

	public IDataModel getDataModel() {
		return wizardInstance.getDataModel();
	}
}
