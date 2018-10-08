package com.tibco.configtool.actions;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.support.XFormControlState;

public class ChooseEnableAction extends OptionAction {
	private String controlId1;
	private String controlId2;
	
	private XFormControlState state1;
	private XFormControlState state2;
	
	protected void selectionAction(boolean selected, XForm form, IActionContext actionContext) {
		if (selected) {
			if (state1 == null) {
				state1 = XFormControlState.disable(form.getUI().getControl(controlId1));
			}
			if (state2 != null) {
				state2.restore();
				state2 = null;
			}
		} else {
			if (state1 != null) {
				state1.restore();
				state1 = null;
			}
			if (state2 == null) {
				state2 = XFormControlState.disable(form.getUI().getControl(controlId2));
			}
		}
	}
}
