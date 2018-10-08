package com.tibco.customwizard.internal.swt;

import org.eclipse.jface.wizard.Wizard;

import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.validator.ValidateResult;

public class CustomWizard extends Wizard {
	private WizardInstance wizardInstance;
	
	public CustomWizard(WizardInstance wizardInstance) {
		this.wizardInstance = wizardInstance;
	}

	public boolean canFinish() {
		ValidateResult validateResult = WizardHelper.getWizardValidateResult(wizardInstance);
		if (validateResult != null && !validateResult.isValid()) {
			return false;
		}

		return super.canFinish();
	}

	public boolean performFinish() {
		return true;
	}
}
