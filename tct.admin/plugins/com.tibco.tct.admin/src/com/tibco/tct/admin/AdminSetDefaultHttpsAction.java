package com.tibco.tct.admin;

import com.tibco.customwizard.config.IDataModel;
import com.tibco.tct.amx.actions.SetDefaultHttpsAction;
import com.tibco.tct.framework.support.TCTContext;

public class AdminSetDefaultHttpsAction extends SetDefaultHttpsAction {
	protected void setDefaultHttps(IDataModel dataModel, String trustStoreFile, String trustStorePassword, String trustStoreType) throws Exception {
		String tibcoConfigHome = TCTContext.getInstance().getTibcoConfigHome();
		String adminEnterpriseName = dataModel.getValue("/admin/baseinfo/enterprisename");
		trustStoreFile = trustStoreFile.replace("${tibco.config.mgmt.home}", tibcoConfigHome);
		trustStoreFile = trustStoreFile.replace("${admin.enterprise.name}", adminEnterpriseName);
		
		super.setDefaultHttps(dataModel, trustStoreFile, trustStorePassword, trustStoreType);
	}
}
