package com.tibco.configtool.actions;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Control;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class CheckNTService implements ICustomAction {
	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		if (!Platform.getOS().equals(Platform.OS_WIN32)) {
			UIControl control = form.getUI().getControl("registerservice");
			((Control) control.getControl()).setVisible(false);
			control = form.getUI().getControl("shortcut");
			((Control) control.getControl()).setVisible(false);
		}
	}
}
