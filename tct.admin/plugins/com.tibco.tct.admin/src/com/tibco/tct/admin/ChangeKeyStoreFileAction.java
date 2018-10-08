package com.tibco.tct.admin;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.UIForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.util.ControlUtils;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.tct.framework.utils.TCTHelper;

public class ChangeKeyStoreFileAction implements ICustomAction {


	public void execute(IActionContext actionContext) {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		UIForm form = xformActionContext.getForm().getUI();
		form.getControl("keystoretype").setXMLValue("AutoDetect");
		form.getControl("keystorepassword").setXMLValue("");
		form.getControl("keyalias").setXMLValue("");
		form.getControl("keypassword").setXMLValue("");
		UIControl control = xformActionContext.getForm().getUI().getControl("aliasgroup");
		ControlUtils.setVisible(control, false);
		control.setEnabled(false);
		TCTHelper.revalidateCurrentPage(actionContext.getWizardInstance());
	}
}
