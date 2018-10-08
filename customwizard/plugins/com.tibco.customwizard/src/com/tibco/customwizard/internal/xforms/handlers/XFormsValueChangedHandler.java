package com.tibco.customwizard.internal.xforms.handlers;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.events.XFormsValueChangedEvent;
import org.nuxeo.xforms.xforms.handlers.XFormEventHandler;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;

import com.tibco.customwizard.internal.xforms.validator.ValidateManager;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.customwizard.validator.ValidateResult;

public class XFormsValueChangedHandler implements XFormEventHandler {
    public final static XFormsValueChangedHandler INSTANCE = new XFormsValueChangedHandler();

    protected XFormsValueChangedHandler() {
    }

    public void handleEvent(XForm form, XFormsEvent event) {
        XFormsValueChangedEvent ev = (XFormsValueChangedEvent) event;
		UIControl control = ev.getControl();

		if (control.getElement() != null) {
			// start our customized validation
			ValidateResult validateResult = ValidateManager.validateControl(control);
			ValidateManager.setValidateResult(control, validateResult);

			WizardHelper.updateWizardButtons(XFormUtils.getWizardInstance(form));
			
			// commit to data model
			control.commit();
		}
		
        XFAction action = control.getAction(event.id);
		if (action != null) {
			action.handleEvent(form, event);
		}
    }
}
