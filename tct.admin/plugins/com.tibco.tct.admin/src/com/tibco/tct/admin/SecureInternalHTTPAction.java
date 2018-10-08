package com.tibco.tct.admin;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.tct.framework.actions.VisibleAction;

public class SecureInternalHTTPAction extends VisibleAction {
	protected void selectionAction(boolean selected, XForm form, IActionContext actionContext) {
		super.selectionAction(selected, form, actionContext);

		IDataModel dataModel = actionContext.getDataModel();

		setHttpURL(selected, dataModel);
	}

	private void setHttpURL(boolean selected, IDataModel dataModel) {
		String httpURL = dataModel.getValue("/admin/serverconnsetting/baseurl");
		if (selected) {
			httpURL = httpURL.replace("http:", "https:");
		} else {
			httpURL = httpURL.replace("https:", "http:");
		}
		dataModel.setValue("/admin/serverconnsetting/baseurl", httpURL);
	}
}
