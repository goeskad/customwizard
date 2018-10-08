package com.tibco.tct.admin;

import org.nuxeo.xforms.ui.UIControl;

import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.validator.ICustomValidator;
import com.tibco.customwizard.validator.IValidationContext;
import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.IXFormValidationContext;

public class LoadBalancerURLValidator implements ICustomValidator {
	public ValidateResult validate(IValidationContext validationContext) {
		return validate(((IXFormValidationContext) validationContext).getControl(), validationContext.getWizardInstance()
				.getDataModel());
	}

	protected ValidateResult validate(UIControl control, IDataModel dataModel) {
		if (control.getXMLValue().contains("/amxadministrator")) {
			return new ValidateResult("The Load Balancer should be \"http://<host>:<port>\", please remove /amxadministrator...");
		}
		return ValidateResult.VALID;
	}
}
