package com.tibco.configtool.actions;

import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;

public class CheckSavedDataModelAction implements ICustomAction {
	public void execute(IActionContext actionContext) throws Exception {
		TCTHelper.getDataModelFile(actionContext);
	}
}
