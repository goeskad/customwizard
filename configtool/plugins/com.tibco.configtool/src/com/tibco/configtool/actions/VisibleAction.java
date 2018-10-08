package com.tibco.configtool.actions;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.util.ControlUtils;

public class VisibleAction extends OptionAction {
	private String controlId;

	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	protected void selectionAction(boolean selected, XForm form, IActionContext actionContext) {
		UIControl control = form.getUI().getControl(controlId);
		
		ControlUtils.setVisible(control, selected);
		control.setEnabled(selected);
		
		TCTHelper.revalidateCurrentPage(actionContext.getWizardInstance());
	}
}
