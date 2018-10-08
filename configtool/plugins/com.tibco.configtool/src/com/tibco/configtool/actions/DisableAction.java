package com.tibco.configtool.actions;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.support.XFormControlState;

public class DisableAction extends OptionAction {
	protected String controlId;

	protected boolean disableWhenChecked = true;

	private XFormControlState state;

	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	public void setDisableWhenChecked(boolean disableWhenChecked) {
		this.disableWhenChecked = disableWhenChecked;
	}

	protected void selectionAction(boolean selected, XForm form, IActionContext actionContext) {
		form.getUI().getControl(controlId).setEnabled(true);
		
		if ((selected && disableWhenChecked) || (!selected && !disableWhenChecked)) {
			if (TCTContext.getInstance().isConsoleMode()) {
				form.getUI().getControl(controlId).setEnabled(false);
			} else {
				if (state == null) {
					state = XFormControlState.disable(form.getUI().getControl(controlId));
				}
			}
		} else {
			if (state != null) {
				state.restore();
				state = null;
			}
		}
		TCTHelper.revalidateCurrentPage(actionContext.getWizardInstance());
	}
}
