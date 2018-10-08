package com.tibco.customwizard.internal.xforms.actions;

import java.net.URL;

import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nuxeo.xforms.xforms.events.UIControlEvent;
import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;

import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;

public class OpenURLAction implements XFAction {
    public void handleEvent(XForm form, XFormsEvent event) {
    	WizardInstance wizardInstance = XFormUtils.getWizardInstance(form);
    	UIControlEvent ev = (UIControlEvent) event;
    	Hyperlink hyperlink = (Hyperlink)ev.getControl().getControl();
        try {
        	WizardHelper.openURL(wizardInstance, (URL)hyperlink.getHref());
        } catch (Exception e) {
        	wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
        }
    }

}