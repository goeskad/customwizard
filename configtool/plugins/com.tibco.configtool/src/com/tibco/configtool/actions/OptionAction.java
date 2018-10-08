package com.tibco.configtool.actions;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.xforms.IXFormActionContext;

public abstract class OptionAction implements ICustomAction {
	private String optionControlId = "optioncontrol";

	public void setOptionControlId(String optionControlId) {
		this.optionControlId = optionControlId;
	}

	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		UIControl optionControl = getOptionControl(form);
		optionControl.validate();
		optionControl.commit();

		boolean selected = Boolean.parseBoolean(optionControl.getXMLValue());
		selectionAction(selected, form, actionContext);
	}

	protected UIControl getOptionControl(XForm form) {
		return form.getUI().getControl(optionControlId);
	}

	protected abstract void selectionAction(boolean selected, XForm form, IActionContext actionContext) throws Exception;
}