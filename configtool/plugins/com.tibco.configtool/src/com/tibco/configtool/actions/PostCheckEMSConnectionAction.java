package com.tibco.configtool.actions;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;

public class PostCheckEMSConnectionAction extends TestEMSConnectionAction {
	private String pageId;

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public void execute(IActionContext actionContext) throws Exception {
		PageInstance pageInstance = WizardHelper.getPageInstanceById(actionContext.getWizardInstance(), pageId);
		XForm form = XFormUtils.getXForm(pageInstance);

		messageProvider = TCTHelper.getMessageProvider(actionContext.getWizardInstance());
		
		testTibcoEMSConnection(form);
	}
}
