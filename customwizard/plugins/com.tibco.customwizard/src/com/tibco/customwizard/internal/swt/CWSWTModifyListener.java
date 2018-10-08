package com.tibco.customwizard.internal.swt;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.nuxeo.xforms.ui.swt.SWTForm;
import org.nuxeo.xforms.ui.swt.proxies.SWTControlProxy;

import com.tibco.customwizard.xforms.events.ModifyEvent;

public class CWSWTModifyListener implements Listener {
	public static final CWSWTModifyListener INSTANCE = new CWSWTModifyListener();

	public void handleEvent(Event event) {
		Widget widget = (Control) event.widget;
		SWTControlProxy control = (SWTControlProxy) widget.getData(SWTForm.CONTROL_KEY);

		if (control.requiresValidation()) {
			control.getForm().fireValidateEvent(control);
		}

		control.getForm().getForm().getProcessor().dispatch(new ModifyEvent(control, event));
	}
}
