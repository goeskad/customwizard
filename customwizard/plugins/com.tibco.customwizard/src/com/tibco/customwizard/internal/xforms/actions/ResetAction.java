package com.tibco.customwizard.internal.xforms.actions;

import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;

public class ResetAction implements XFAction {
	public void handleEvent(XForm form, XFormsEvent event) {
		form.reset();
	}
}
