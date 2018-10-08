package com.tibco.configtool.sslwizard;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class ModifyPasswordAction implements ICustomAction {
	public static String keystorePassword;
	
	public void execute(IActionContext actionContext) throws Exception {
		XForm xform = ((IXFormActionContext) actionContext).getForm();
		keystorePassword = xform.getUI().getControl("password").getXMLValue();
	}
}
