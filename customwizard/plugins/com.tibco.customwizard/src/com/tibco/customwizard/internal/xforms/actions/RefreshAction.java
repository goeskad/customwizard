package com.tibco.customwizard.internal.xforms.actions;

import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;

import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;

public class RefreshAction implements XFAction {
	public void handleEvent(XForm form, XFormsEvent event) {
		PageInstance pageInstance = XFormUtils.getPageInstance(form);
		WizardInstance wizardInstance = XFormUtils.getWizardInstance(form);
		try {
			WizardHelper.refreshPage(pageInstance);
		} catch (Exception e) {
			wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
		}
	}
}
