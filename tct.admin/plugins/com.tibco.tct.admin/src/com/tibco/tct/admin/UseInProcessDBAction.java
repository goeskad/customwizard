package com.tibco.tct.admin;

import java.util.List;

import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.devtools.installsupport.commands.tpclshell.InstantiatedTPCLShellConfig;
import com.tibco.tct.amx.utils.MachineModelUtils;
import com.tibco.tct.amx.utils.TPCLShellsUtils;
import com.tibco.tct.framework.actions.UseDefaultAction;

public class UseInProcessDBAction extends UseDefaultAction {
	private static InstantiatedTPCLShellConfig hsqlShell;

	public UseInProcessDBAction() {
		setCopyFromPrefix("default");
	}
	
	public void copyDefault(IDataModel dataModel) {
		setInProcessDBData(dataModel);
	}

	public static void setInProcessDBData(IDataModel dataModel) {
		if (hsqlShell == null) {
			try {
				List<InstantiatedTPCLShellConfig> shellConfigList = TPCLShellsUtils.queryInstantiatedTPCLShellConfig(MachineModelUtils.getMachine(),
						TPCLShellsUtils.SHELL_TYPE_JDBC);
				for (InstantiatedTPCLShellConfig shellConfig : shellConfigList) {
					if (TPCLShellsUtils.isHSQL(shellConfig)) {
						if (hsqlShell == null || shellConfig.getOwningRUMemberVersion().compareTo(hsqlShell.getOwningRUMemberVersion()) > 0) {
							hsqlShell = shellConfig;
						}
					}
				}
			} catch (Exception e) {
				throw new ActionException(e);
			}
		}
		dataModel.setValue("/admin/database/enablessl", "false");
		dataModel.setValue("/admin/database/vendor", TPCLShellsUtils.getShellIdentifier(hsqlShell));
		dataModel.setValue("/admin/database/url", hsqlShell.getProperties().getProperty(TPCLShellsUtils.JDBC_DEFAULT_URL));
		dataModel.setValue("/admin/database/username", "sa");
		dataModel.setValue("/admin/database/password", "#!t0Fkc4qsDN4UaA18oNF2LA==");
	}
}
