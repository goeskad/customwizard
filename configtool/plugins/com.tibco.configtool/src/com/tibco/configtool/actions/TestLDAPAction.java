package com.tibco.configtool.actions;

import java.util.Properties;

import javax.naming.ldap.InitialLdapContext;

import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.support.PasswordObfuscator;
import com.tibco.configtool.utils.LDAPUtils;
import com.tibco.configtool.utils.SSLUtils;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.IBackgroudAction;
import com.tibco.customwizard.util.WizardHelper;

public class TestLDAPAction implements ICustomAction, IBackgroudAction {
	private String keyPrefix;
	
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public void execute(IActionContext actionContext) throws Exception {
		WizardInstance wizardInstance = actionContext.getWizardInstance();
		try {
			IMessageProvider messageProvider = TCTHelper.getMessageProvider(wizardInstance);
			testLDAPConnection(actionContext.getDataModel());
			WizardHelper.openMessage(wizardInstance, messageProvider.getMessage("testconnection.success"));
		} catch (Exception e) {
			WizardHelper.openErrorDialog(wizardInstance, e);
		}
	}

	private void testLDAPConnection(IDataModel dataModel) throws Exception {
		String factory = dataModel.getValue(keyPrefix + "/factory");
		if (factory == null) {
			factory = "com.sun.jndi.ldap.LdapCtxFactory";
		}
		String url = dataModel.getValue(keyPrefix + "/url");
		String username = dataModel.getValue(keyPrefix + "/username");
		String password = dataModel.getValue(keyPrefix + "/password");
		password = new PasswordObfuscator().decrypt(password);
		String authenticationtype = dataModel.getValue(keyPrefix + "/authenticationtype");
		if (authenticationtype == null) {
			authenticationtype = "simple";
		} else {
			authenticationtype = authenticationtype.toLowerCase();
		}
		String timeout = dataModel.getValue(keyPrefix + "/timeout");
		
		Properties props = new Properties();
		LDAPUtils.packBaseProperties(props, factory, url, username, password, authenticationtype, timeout);
		packSSLProperties(props, dataModel);
		
		InitialLdapContext ctx = null;
		try {
			ctx = LDAPUtils.getInitialLdapContext(props);
		} catch (Exception e) {
			throw new ActionException("Failed to validate LDAP connection info due to: " + e.getMessage());
		}

		String baseDN = dataModel.getValue(keyPrefix + "/user/basedn");
		if (baseDN != null) {
			String filter = dataModel.getValue(keyPrefix + "/user/filter");
			boolean subtree = Boolean.parseBoolean(dataModel.getValue(keyPrefix + "/user/subtree"));

			String logonUser = dataModel.getValue("/admin/authenticationrealm/username");
			String logonpassword = dataModel.getValue("/admin/authenticationrealm/password");
			logonpassword = new PasswordObfuscator().decrypt(logonpassword);
			try {
				LDAPUtils.checkUser(ctx, logonUser, logonpassword, baseDN, filter, subtree);
			} finally {
				ctx.close();
			}
		}
	}
	
	private void packSSLProperties(Properties info, IDataModel dataModel) throws Exception {
		boolean enablessl = Boolean.parseBoolean(dataModel.getValue(keyPrefix + "/enablessl"));
		if (enablessl) {
			String location = dataModel.getValue(keyPrefix + "/keystorelocation");
			String type = dataModel.getValue(keyPrefix + "/keystoretype");
			String password = dataModel.getValue(keyPrefix + "/keystorepassword");

			LDAPUtils.packSSLProperties(info, SSLUtils.getSocketFactoryClassName(location, password, type));
		}
	}
}
