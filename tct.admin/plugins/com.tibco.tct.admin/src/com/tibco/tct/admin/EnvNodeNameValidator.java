package com.tibco.tct.admin;

import org.nuxeo.xforms.ui.UIControl;

import com.tibco.customwizard.validator.ICustomValidator;
import com.tibco.customwizard.validator.IValidationContext;
import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.IXFormValidationContext;

public class EnvNodeNameValidator implements ICustomValidator {
	private static final String ALPHABET = "[a-zA-Z]";
	
	private static final String ALPHANUMERIC = "[a-zA-Z0-9]+";
	
	/**
	 * validate the env name or node name. Environment Name or Node Name should
	 * be alphanumeric and always should start with Alphabet.
	 * 
	 * @param the context which should be validated
	 * @return the message to display
	 */
	public ValidateResult validate(IValidationContext validationContext) {
		UIControl uc = ((IXFormValidationContext) validationContext).getControl();
		String fieldName = uc.getName();
		String value = uc.getXMLValue();
		if (value == null || value.length() == 0) {
			return new ValidateResult(fieldName + " must be nonempty.");
		}

		if (!value.substring(0, 1).matches(ALPHABET)){
			return new ValidateResult(fieldName + " must start with Alphabet.");
		}

		if (!value.matches(ALPHANUMERIC)){
			return new ValidateResult(fieldName + " must be alphanumeric.");
		}
		
		return ValidateResult.VALID;
	}
}
