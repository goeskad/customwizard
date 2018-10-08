package com.tibco.tct.admin;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.util.WizardHelper;

public class SetPageFlowAction implements ICustomAction {
	public void execute(IActionContext actionContext) throws Exception {
		String authenticationrealm = actionContext.getDataModel().getValue("/admin/authenticationrealm/type");
		WizardHelper.setWizardPageByGroupIds(actionContext.getWizardInstance(), new String[] { "part1", "part2", authenticationrealm, "security",
				"part3" });
	}
}
