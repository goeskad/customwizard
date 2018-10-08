package com.tibco.configtool.sslwizard;

import java.text.MessageFormat;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.actions.OpenSSLWizardAction;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class SetLabelWithHostPortAction implements ICustomAction {
	public void execute(IActionContext actionContext) throws Exception {
		XForm xform = ((IXFormActionContext) actionContext).getForm();

		OpenSSLWizardAction sourceAction = OpenSSLWizardAction.getOpenSSLWizardAction(actionContext.getWizardInstance());

		UIControl label = xform.getUI().getControl("intro");
		label.setXMLValue(MessageFormat.format(label.getXMLValue(), sourceAction.getHost(), sourceAction.getPort()));
		
		String keystorePassword = ModifyPasswordAction.keystorePassword;
		xform.getUI().getControl("password").setXMLValue(keystorePassword);
		xform.getUI().getControl("confirmpassword").setXMLValue(keystorePassword);
	}
}
