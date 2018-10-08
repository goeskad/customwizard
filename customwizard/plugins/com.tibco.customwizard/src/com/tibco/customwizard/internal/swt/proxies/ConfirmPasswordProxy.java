package com.tibco.customwizard.internal.swt.proxies;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nuxeo.xforms.ui.swt.BuildContext;

import com.tibco.customwizard.internal.xforms.validator.MatchValueValidator;
import com.tibco.customwizard.internal.xforms.validator.ValidateManager;

public class ConfirmPasswordProxy extends PasswordProxy {
	public Control create(BuildContext context, Composite parent, int style) {
		Control control = super.create(context, parent, style);

		String controlId = context.element.getAttribute("controlId");
		PasswordProxy refControl = (PasswordProxy) context.form.getControl(controlId);
		refControl.setConfirmPasswordProxy(this);
		
		MatchValueValidator validator = new MatchValueValidator();
		validator.setControl(refControl);
		validator.setMatchControl(this);
		validator.setErrorMessage(context.element.getAttribute("errorMessage"));
		ValidateManager.addValidator(context.element, validator);
		ValidateManager.addValidator(refControl.getElement(), validator);

		return control;
	}

}
