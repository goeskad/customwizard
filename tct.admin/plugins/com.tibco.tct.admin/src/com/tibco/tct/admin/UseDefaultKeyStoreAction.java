package com.tibco.tct.admin;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.tct.framework.actions.UseDefaultAction;

public class UseDefaultKeyStoreAction extends UseDefaultAction {
	protected void selectionAction(boolean selected, XForm form, IActionContext actionContext) {
		super.selectionAction(selected, form, actionContext);
		
		form.getUI().getControl("commonname").setEnabled(selected);
	}
}
