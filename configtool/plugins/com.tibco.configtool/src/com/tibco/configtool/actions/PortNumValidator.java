package com.tibco.configtool.actions;

import org.nuxeo.xforms.ui.UIControl;

import com.tibco.customwizard.validator.ICustomValidator;
import com.tibco.customwizard.validator.IValidationContext;
import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.IXFormValidationContext;

/**
 * 
 * @author tozhang
 *
 */
public class PortNumValidator implements ICustomValidator{

	/**
	 * validate the value of port number. The value should be not null, positive
	 * integer & the range is between 0 ~ 99999 .
	 * 
	 * @param the context which should be validated
	 * @return the message to display
	 */
	public ValidateResult validate(IValidationContext validationContext) {
		UIControl uc = ((IXFormValidationContext)validationContext).getControl();
		String fieldName = uc.getName();
		String value = uc.getXMLValue();
		if (value == null || value.equals("") || !value.matches("[0-9]*")) {
			return new ValidateResult(fieldName + " The value should be an integer.");
		}
		long port = Long.parseLong(value);
		if (port <= 0 || port > 99999) {
			return new ValidateResult(fieldName + " The value should between 1 and 99999 .");
		}

		return ValidateResult.VALID;
	}
	
}
