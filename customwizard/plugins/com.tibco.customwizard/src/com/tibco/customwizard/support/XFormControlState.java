package com.tibco.customwizard.support;

import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.swt.widgets.Control;
import org.nuxeo.xforms.ui.UIControl;

import com.tibco.customwizard.internal.console.proxies.ConsoleControlProxy;

public class XFormControlState {
	private ControlEnableState state;
	private UIControl control;

	public XFormControlState(UIControl control) {
		this.control = control;
		if (control instanceof ConsoleControlProxy) {
			control.setEnabled(false);
		} else {
			state = ControlEnableState.disable((Control) control.getControl());
		}
	}

	public static XFormControlState disable(UIControl control) {
		return new XFormControlState(control);
	}

	public void restore() {
		if (control instanceof ConsoleControlProxy) {
			control.setEnabled(true);
		} else {
			state.restore();
		}
	}
}
