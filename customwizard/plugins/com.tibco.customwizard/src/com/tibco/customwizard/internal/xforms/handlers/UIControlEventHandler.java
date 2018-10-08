package com.tibco.customwizard.internal.xforms.handlers;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.events.UIControlEvent;
import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.handlers.XFormEventHandler;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;

public class UIControlEventHandler implements XFormEventHandler {
    public final static UIControlEventHandler INSTANCE = new UIControlEventHandler();

    protected UIControlEventHandler() {
    }

    public void handleEvent(XForm form, XFormsEvent event) {
        UIControlEvent ev = (UIControlEvent) event;
        UIControl element = ev.getControl();
        if (element != null) {
			XFAction action = element.getAction(event.id);
			if (action != null) {
				action.handleEvent(form, event);
			}
        }
    }
}