package com.tibco.configtool.support;

import com.tibco.customwizard.action.IActionContext;

public interface ICleanupableAction {
	public void executeCleanup(IActionContext actionContext) throws Exception;
}
