package com.tibco.configtool.actions;

import java.io.File;

import org.nuxeo.xforms.xforms.events.UIControlEvent;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class SaveDataModelDialogAction implements ICustomAction {
	public void execute(IActionContext actionContext) throws Exception {
		WizardInstance wizardInstance = actionContext.getWizardInstance();

		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;

		String sessionDir = ((UIControlEvent) xformActionContext.getEvent()).getControl().getXMLValue();
		SaveDataModelAction.getSaveDataModelAction(wizardInstance).setSessionDir(new File(sessionDir));
	}
}
