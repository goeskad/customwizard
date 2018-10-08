package com.tibco.configtool.actions;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.events.UIControlEvent;

import com.tibco.configtool.utils.TCTHelper;
import com.tibco.configtool.utils.WindowsRegUtils;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.MessageType;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class CheckUACAction implements ICustomAction {
	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		UIControl control = ((UIControlEvent) xformActionContext.getEvent()).getControl();

		boolean selected = Boolean.parseBoolean(control.getXMLValue());
		if (selected) {
			String strUACValue = WindowsRegUtils.getDWordValue("HKLM\\Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\System", "EnableLUA");
			if (strUACValue != null) {
				int intUACValue = Integer.parseInt(strUACValue);
				if (intUACValue != 0) {
					WizardInstance wizardInstance = actionContext.getWizardInstance();
					String errorMessage = TCTHelper.getMessageProvider(wizardInstance).getMessage("CheckUACAction.warning");
					WizardHelper.openMessage(wizardInstance, errorMessage, MessageType.WARNING, false);
				}
			}
		}
	}
}
