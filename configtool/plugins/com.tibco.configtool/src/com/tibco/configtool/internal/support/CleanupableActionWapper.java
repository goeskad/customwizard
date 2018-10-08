package com.tibco.configtool.internal.support;

import com.tibco.configtool.support.ICleanupableAction;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;

public class CleanupableActionWapper implements ICustomAction {
	private ICleanupableAction action;

	public CleanupableActionWapper(ICleanupableAction action) {
		this.action = action;
	}

	public void execute(IActionContext actionContext) throws Exception {
		action.executeCleanup(actionContext);
	}
}
