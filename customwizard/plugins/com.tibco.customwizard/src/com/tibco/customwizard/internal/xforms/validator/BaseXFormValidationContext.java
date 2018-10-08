package com.tibco.customwizard.internal.xforms.validator;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.customwizard.xforms.IXFormValidationContext;

public class BaseXFormValidationContext implements IXFormValidationContext {
	private WizardInstance wizardInstance;
	private XForm form;
	private UIControl control;

	public BaseXFormValidationContext(UIControl control) {
		this.form = control.getElement().getForm();
		this.wizardInstance = XFormUtils.getWizardInstance(form);
		this.control = control;
	}

	public WizardInstance getWizardInstance() {
		return wizardInstance;
	}

	public XForm getForm() {
		return form;
	}

	public UIControl getControl() {
		return control;
	}
}
