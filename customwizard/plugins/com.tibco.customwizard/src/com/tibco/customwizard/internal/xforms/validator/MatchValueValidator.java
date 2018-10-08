package com.tibco.customwizard.internal.xforms.validator;

import org.nuxeo.xforms.ui.UIControl;

import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.IXFormValidationContext;

public class MatchValueValidator extends XFormControlValidator {
	private UIControl control;

	private UIControl matchControl;
	
	private String errorMessage;

	public void setControl(UIControl control) {
		this.control = control;
	}

	public void setMatchControl(UIControl matchControl) {
		this.matchControl = matchControl;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ValidateResult validate(UIControl control, IXFormValidationContext validationContext) {
		String value = this.control.getXMLValue();
		String matchValue = getMatchValue(control);
		boolean match = (value == matchValue || (value != null && matchValue != null && value.equals(matchValue)));
		if (!match) {
			if (errorMessage == null) {
				errorMessage = matchControl.getName() + " doesn't match \"" + this.control.getName() + "\"";
			}
			return new ValidateResult(errorMessage);
		}
		
		ValidateManager.setValidateResult(this.control, ValidateResult.VALID);
		ValidateManager.setValidateResult(matchControl, ValidateResult.VALID);
		
		return ValidateResult.VALID;
	}

	protected String getMatchValue(UIControl control) {
		return matchControl.getXMLValue();
	}
}
