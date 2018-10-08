package com.tibco.customwizard.xforms;

import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;

public interface IXFormActionContext extends IActionContext {
	XForm getForm();

	XFormsEvent getEvent();
}
