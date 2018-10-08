package com.tibco.tct.admin;

import java.util.HashSet;
import java.util.Set;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.util.ControlUtils;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.tct.framework.utils.TCTHelper;

public class CredentialAliasAction implements ICustomAction {

	public void execute(IActionContext actionContext) {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		IDataModel dataModel = actionContext.getWizardInstance().getDataModel();
		XForm form = xformActionContext.getForm();
		
		UIControl aliasControl = form.getUI().getControl("keyalias");
		UIControl keysotreControl = form.getUI().getControl("credentialkeystore");
		keysotreControl.setEnabled(true);
		
		UIControl groupControl = form.getUI().getControl("aliasgroup");
		String aliasValue = dataModel.getValue("/admin/credentialkeystore/keyalias");
		
		boolean enable = !aliasValue.isEmpty();
		if (enable) {
			Set<String> aliasNames = new HashSet<String>();
			aliasNames.add(aliasValue);
			TCTHelper.updateCombo(aliasControl, aliasNames);
		}
		ControlUtils.setVisible(groupControl, enable);
		groupControl.setEnabled(enable);

	}
}
