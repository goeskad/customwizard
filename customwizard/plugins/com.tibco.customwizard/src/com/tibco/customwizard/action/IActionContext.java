package com.tibco.customwizard.action;

import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.instance.WizardInstance;

public interface IActionContext {
	public WizardInstance getWizardInstance();
	public IDataModel getDataModel();
}
