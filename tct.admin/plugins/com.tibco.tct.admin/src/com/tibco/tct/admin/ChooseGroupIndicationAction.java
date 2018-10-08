package com.tibco.tct.admin;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.tct.framework.utils.TCTHelper;

public class ChooseGroupIndicationAction implements ICustomAction {
	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		UIControl control = form.getUI().getControl("indication");
		String value = control.getXMLValue();

		String[] controls = new String[] { "basedn", "filter", "groupuserattr", "groupattr", "subgroupattr", "userAttributeGroupsName" };

		if (value.equals("groupHasUsers")) {
			for (String controlId : controls) {
				if (controlId.equals("userAttributeGroupsName")) {
					form.getUI().getControl(controlId).setEnabled(false);
				} else {
					form.getUI().getControl(controlId).setEnabled(true);
				}
			}
		} else if (value.equals("userHasGroups")) {
			for (String controlId : controls) {
				if (controlId.equals("groupuserattr")) {
					form.getUI().getControl(controlId).setEnabled(false);
				} else {
					form.getUI().getControl(controlId).setEnabled(true);
				}
			}
		} else if (value.equals("userDNHasGroups")) {
			for (String controlId : controls) {
				if (controlId.equals("userAttributeGroupsName")) {
					form.getUI().getControl(controlId).setEnabled(true);
				} else {
					form.getUI().getControl(controlId).setEnabled(false);
				}
			}
		}

		TCTHelper.revalidateCurrentPage(actionContext.getWizardInstance());
	}
}
