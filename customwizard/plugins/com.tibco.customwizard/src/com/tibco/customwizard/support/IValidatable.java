package com.tibco.customwizard.support;

import com.tibco.customwizard.validator.ValidateResult;

public interface IValidatable {
	public ValidateResult getValidateResult();
	public void validate();
}
