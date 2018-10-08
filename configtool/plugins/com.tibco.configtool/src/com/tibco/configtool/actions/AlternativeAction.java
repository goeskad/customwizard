package com.tibco.configtool.actions;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.support.XFormControlState;

public class AlternativeAction extends OptionAction {
	private String controlId1;
	private String controlId2;

	private XFormControlState state1;
	private XFormControlState state2;

	public void setControlId1(String controlId1) {
		this.controlId1 = controlId1;
	}

	public void setControlId2(String controlId2) {
		this.controlId2 = controlId2;
	}

	protected void selectionAction(boolean selected, XForm form, IActionContext actionContext) {
		if (selected) {
			if (state2 == null) {
				state2 = XFormControlState.disable(form.getUI().getControl(controlId2));
			}
			if (state1 != null) {
				state1.restore();
				state1 = null;
				form.getUI().getControl(controlId1).setEnabled(true);
			}
		} else {
			if (state1 == null) {
				state1 = XFormControlState.disable(form.getUI().getControl(controlId1));
			}
			if (state2 != null) {
				state2.restore();
				state2 = null;
				form.getUI().getControl(controlId2).setEnabled(true);
			}
		}
		TCTHelper.revalidateCurrentPage(actionContext.getWizardInstance());
	}
}
