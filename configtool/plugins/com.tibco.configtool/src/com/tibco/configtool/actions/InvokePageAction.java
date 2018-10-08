package com.tibco.configtool.actions;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.util.WizardHelper;

public class InvokePageAction implements ICustomAction {
	private String pageId;
	private boolean preAction = true;
	private boolean postAction = false;

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public void setPreAction(boolean preAction) {
		this.preAction = preAction;
	}

	public void setPostAction(boolean postAction) {
		this.postAction = postAction;
	}

	public void execute(IActionContext actionContext) throws Exception {
		PageInstance pageInstance = WizardHelper.getPageInstanceById(actionContext.getWizardInstance(), pageId);
		if (preAction) {
			pageInstance.performPreAction();
		}

		if (postAction) {
			pageInstance.performPostAction();
		}
	}
}
