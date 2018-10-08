package com.tibco.tct.admin;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.tct.framework.actions.OptionAction;
import com.tibco.tct.framework.utils.TCTHelper;

public class SecureCommunicationAction extends OptionAction {
	public SecureCommunicationAction() {
	}

	protected void selectionAction(boolean selected, XForm form, IActionContext actionContext) throws Exception {
		IDataModel dataModel = actionContext.getDataModel();

		String strSelected = selected + "";
		dataModel.setValue("/admin/serverconnsetting/enablessl", strSelected);
		dataModel.setValue("/admin/internalhttpconn/isSecuredWithTCS", strSelected);
		dataModel.setValue("/admin/emsconfig/enablessl", strSelected);
		dataModel.setValue("/admin/database/enablessl", strSelected);
		dataModel.setValue("/admin/dbrealm/enablessl", strSelected);
		dataModel.setValue("/admin/ldaprealm/enablessl", strSelected);
		dataModel.setValue("/admin/mcrinfo/emsconfig/enablessl", strSelected);
		dataModel.setValue("/admin/mcrinfo/database/enablessl", strSelected);
		dataModel.setValue("/admin/clinfo/logsrvdatabase/enablessl", strSelected);
		dataModel.setValue("/admin/clinfo/payloadsrvdatabase/enablessl", strSelected);

		if (dataModel.getValue("/admin/database/usedefault").equals("true")) {
			dataModel.setValue("/admin/database/enablessl", "false");
		}

		TCTHelper.preValidatePage(actionContext.getWizardInstance(), "emsconfig");
	}
}
