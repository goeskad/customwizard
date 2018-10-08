package com.tibco.customwizard.internal.xforms.validator;

import org.nuxeo.xforms.ui.UIControl;

import com.tibco.customwizard.validator.ICustomValidator;
import com.tibco.customwizard.validator.IValidationContext;
import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.IXFormValidationContext;

public abstract class XFormControlValidator implements ICustomValidator {
	public ValidateResult validate(IValidationContext validationContext) {
		IXFormValidationContext xformValidationContext = (IXFormValidationContext) validationContext;
		return validate(xformValidationContext.getControl(), xformValidationContext);
	}

	protected abstract ValidateResult validate(UIControl control, IXFormValidationContext validationContext);
}
