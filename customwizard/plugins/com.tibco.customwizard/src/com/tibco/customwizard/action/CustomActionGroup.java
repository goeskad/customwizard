package com.tibco.customwizard.action;

import java.util.ArrayList;
import java.util.List;

import com.tibco.customwizard.util.WizardHelper;

public class CustomActionGroup implements ICustomAction {
	private List<ICustomAction> actionList = new ArrayList<ICustomAction>();

	public void execute(IActionContext actionContext) throws Exception {
		for (ICustomAction action : actionList) {
			WizardHelper.executeAction(actionContext, action);
		}
	}

	public List<ICustomAction> getActionList() {
		return actionList;
	}

	public void addAction(ICustomAction postAction) {
		actionList.add(postAction);
	}
}
