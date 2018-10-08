package com.tibco.customwizard.xforms;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.validator.IValidationContext;

public interface IXFormValidationContext extends IValidationContext{
	XForm getForm();
	UIControl getControl();
}
