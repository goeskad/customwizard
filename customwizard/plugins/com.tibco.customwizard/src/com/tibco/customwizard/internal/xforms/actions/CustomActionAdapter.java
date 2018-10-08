package com.tibco.customwizard.internal.xforms.actions;

import org.nuxeo.xforms.xforms.model.actions.XFAction;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class CustomActionAdapter implements ICustomAction {
	private XFAction action;

	public CustomActionAdapter(XFAction action) {
		this.action = action;
	}

	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		action.handleEvent(xformActionContext.getForm(), xformActionContext.getEvent());
	}
}
