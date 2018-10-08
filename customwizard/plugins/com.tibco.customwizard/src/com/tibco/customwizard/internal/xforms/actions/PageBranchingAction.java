package com.tibco.customwizard.internal.xforms.actions;

import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;

import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;

public class PageBranchingAction implements XFAction {
    private String ref;

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void handleEvent(XForm form, XFormsEvent event) {
    	WizardInstance wizardInstance = XFormUtils.getWizardInstance(form);
        try {
            String pageId = wizardInstance.getDataModel().getValue(ref);
            WizardHelper.replaceNextPage(wizardInstance,pageId);
        } catch (Exception e) {
        	wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
        }
    }
}
