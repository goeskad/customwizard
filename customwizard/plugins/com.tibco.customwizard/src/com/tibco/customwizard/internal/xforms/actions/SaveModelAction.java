package com.tibco.customwizard.internal.xforms.actions;

import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;

import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.DataModelHelper;
import com.tibco.customwizard.util.XFormUtils;

public class SaveModelAction implements XFAction {
    public void handleEvent(XForm form, XFormsEvent event) {
    	WizardInstance wizardInstance = XFormUtils.getWizardInstance(form);
        try {
        	DataModelHelper.saveDataModel(wizardInstance);
        } catch (Exception e) {
        	wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
        }
    }

}
