package com.tibco.ert.ui.customwizard;

import com.tibco.customwizard.config.IDataModel;
import com.tibco.ert.model.core.AdminAccessConfig;
import com.tibco.ert.model.core.PasswordObfuscator;

public class CWERTUtils {
	public static AdminAccessConfig createAdminAccessConfig(IDataModel dataModel, String prefix) throws Exception {
		return new AdminAccessConfig(dataModel.getValue(prefix + "/host"), Integer.parseInt(dataModel.getValue(prefix
				+ "/port")), dataModel.getValue(prefix + "/username"), PasswordObfuscator.instance.decrypt(dataModel
				.getValue(prefix + "/password")), PasswordObfuscator.instance.decrypt(dataModel.getValue(prefix
				+ "/dbpassword")));
	}
}
