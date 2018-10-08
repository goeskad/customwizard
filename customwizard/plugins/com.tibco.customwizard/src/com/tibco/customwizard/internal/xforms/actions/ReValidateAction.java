package com.tibco.customwizard.internal.xforms.actions;

import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;

import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;

public class ReValidateAction implements XFAction {
	public void handleEvent(XForm form, XFormsEvent event) {
		PageInstance pageInstance = XFormUtils.getPageInstance(form);
		WizardHelper.validate(pageInstance);
		WizardHelper.updateWizardButtons(pageInstance.getWizardInstance());
	}
}
