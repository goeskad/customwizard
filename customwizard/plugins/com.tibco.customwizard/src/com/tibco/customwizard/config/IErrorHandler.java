package com.tibco.customwizard.config;

import com.tibco.customwizard.instance.WizardInstance;



public interface IErrorHandler {
	void handleError(WizardInstance wizardInstance, Throwable error);
}
