package com.tibco.configtool.actions;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class TestTCSConnectionAction implements ICustomAction {
	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		WizardInstance wizardInstance = actionContext.getWizardInstance();
		
		String protocol = "service:jmx:jmxmp";
		String[] hostPorts;
		UIControl hostPortControl = form.getUI().getControl("hostportlist");
		if (hostPortControl != null) {
			hostPorts = hostPortControl.getXMLValue().split(",");
		} else {
			hostPorts = new String[] { form.getUI().getControl("host").getXMLValue() + ":" + form.getUI().getControl("port").getXMLValue() };
		}
		String username = form.getUI().getControl("username").getXMLValue();
		String password = form.getUI().getControl("password").getXMLValue();

		try {
			for (String hostPort : hostPorts) {
				testTibcoTCSConnection(protocol + "://" + hostPort, username, password);
			}
			WizardHelper.openMessage(wizardInstance, "Test connection successful!");
		} catch (Exception e) {
			WizardHelper.openErrorDialog(wizardInstance, e);
		}
	}

	private void testTibcoTCSConnection(String url, String username, String password) {

	}
}
