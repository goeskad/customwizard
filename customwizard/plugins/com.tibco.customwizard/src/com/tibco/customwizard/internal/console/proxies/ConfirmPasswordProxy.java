package com.tibco.customwizard.internal.console.proxies;

import org.nuxeo.xforms.ui.UIForm;
import org.nuxeo.xforms.xforms.model.controls.XFControlElement;

import com.tibco.customwizard.internal.console.BuildContext;
import com.tibco.customwizard.internal.xforms.validator.MatchValueValidator;
import com.tibco.customwizard.internal.xforms.validator.ValidateManager;

public class ConfirmPasswordProxy extends PasswordProxy {
	public void create(UIForm form, Object context) {
		super.create(form, context);

		XFControlElement element = ((BuildContext) context).element;
		String controlId = element.getAttribute("controlId");
		PasswordProxy refControl = (PasswordProxy) form.getControl(controlId);
		refControl.setConfirmPasswordProxy(this);

		MatchValueValidator validator = new MatchValueValidator();
		validator.setControl(refControl);
		validator.setMatchControl(this);
		validator.setErrorMessage(element.getAttribute("errorMessage"));
		ValidateManager.addValidator(element, validator);
		ValidateManager.addValidator(refControl.getElement(), validator);
	}
}
