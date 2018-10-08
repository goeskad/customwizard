package com.tibco.configtool.actions;

import com.tibco.configtool.support.PasswordObfuscator;
import com.tibco.configtool.utils.SSLUtils;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.config.IDataModel;

public class SetDefaultHttpsAction extends TCTPostAction {
	private String keyPrefix;
	private String trustStrorePrefix = "keystore";

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public void setTrustStrorePrefix(String trustStrorePrefix) {
		this.trustStrorePrefix = trustStrorePrefix;
	}

	public void execute(IActionContext actionContext) throws Exception {
		IDataModel dataModel = actionContext.getDataModel();

		boolean sslEnabled = Boolean.parseBoolean(dataModel.getValue(keyPrefix + "/" + "enablessl"));
		if (sslEnabled) {
			String trustStoreFile = dataModel.getValue(keyPrefix + "/" + trustStrorePrefix + "location");
			String trustStorePassword = dataModel.getValue(keyPrefix + "/" + trustStrorePrefix + "password");
			String trustStoreType = dataModel.getValue(keyPrefix + "/" + trustStrorePrefix + "type");
			trustStorePassword = new PasswordObfuscator().decrypt(trustStorePassword);

			setDefaultHttps(dataModel, trustStoreFile, trustStorePassword, trustStoreType);
		}
	}

	protected void setDefaultHttps(IDataModel dataModel, String trustStoreFile, String trustStorePassword, String trustStoreType) throws Exception {
		SSLUtils.setDefaultHttps(trustStoreFile, trustStorePassword, trustStoreType);
	}
}
