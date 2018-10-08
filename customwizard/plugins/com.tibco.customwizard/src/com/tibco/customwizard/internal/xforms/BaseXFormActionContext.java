package com.tibco.customwizard.internal.xforms;

import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.internal.support.BaseActionContext;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class BaseXFormActionContext extends BaseActionContext implements IXFormActionContext {
	private XForm form;
	private XFormsEvent event;

	public BaseXFormActionContext(WizardInstance wizardInstance, XForm form, XFormsEvent event) {
		super(wizardInstance);
		this.form = form;
		this.event = event;
	}

	public XForm getForm() {
		return form;
	}

	public XFormsEvent getEvent() {
		return event;
	}
}
