package com.tibco.tct.admin;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.IBackgroundAction;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.tct.amx.actions.IConnectionTester;
import com.tibco.tct.amx.support.PasswordObfuscator;
import com.tibco.tct.framework.support.IMessageProvider;
import com.tibco.tct.framework.utils.TCTHelper;
import com.tibco.trinity.runtime.core.connector.ConnectorUtils;
import com.tibco.trinity.runtime.core.provider.authn.ldap.LdapUserPrincipal;
import com.tibco.trinity.runtime.core.sspi.ProviderNames;
import com.tibco.trinity.runtime.core.sspi.UsernamePasswordCallbackHandler;
import com.tibco.trinity.runtime.core.sspi.authn.LoginConfigurationFactory;

/**
 * Test LDAP's authentication (fully) & realms (partly) by calling Trinity's
 * actual code.  SSL is supported as well
 *
 * @author ashundi
 */
public class TestLDAPConnectionAction implements ICustomAction, IBackgroundAction, IConnectionTester {
	private UserPasswordDialog ud;
	
	public void execute(final IActionContext actionContext) throws Exception {
		WizardInstance wizardInstance = actionContext.getWizardInstance();
		IMessageProvider messageProvider = TCTHelper.getMessageProvider(wizardInstance);
		final IDataModel dataModel = actionContext.getDataModel();
		
		if (WizardHelper.isSWTMode(wizardInstance)) {
			ud = new UserPasswordDialog(wizardInstance);
			if (ud.open() != Dialog.OK) {
				return;
			}
		}
		
		try {
			String msg = testLDAPConnection(dataModel, messageProvider);
			WizardHelper.openMessage(wizardInstance, msg);
		} catch (Exception e) {
			WizardHelper.openErrorDialog(wizardInstance, e);
		}
	}

	public void testConnection(WizardInstance wizardInstance, String pageId) throws Exception {
		IMessageProvider messageProvider = TCTHelper.getMessageProvider(wizardInstance);
		testLDAPConnection(wizardInstance.getDataModel(), messageProvider);
	}

	protected String testLDAPConnection(IDataModel dataModel, IMessageProvider messageProvider) throws Exception {
		Map<String, Object> props = new HashMap<String, Object>();
		LoginContext loginCtx = null;
		try {

			final String LDAP = "com.tibco.trinity.runtime.core.provider.authn.ldap.";
			final String LDAPREALM = "com.tibco.trinity.realm.core.provider.realm.ldap.";

			final PasswordObfuscator deobf = new PasswordObfuscator();

			//database xform
			final String superUser;
			final String superPwd;
			if (ud == null) {
				superUser = dataModel.getValue("/admin/authenticationrealm/username");
				superPwd = deobf.decrypt(dataModel.getValue("/admin/authenticationrealm/password"));
			} else {
				superUser = ud.username;
				superPwd = ud.password;
			}

			//ldaprealm1.xform
			final String adminBindDNName = dataModel.getValue("/admin/ldaprealm/username");
			final String adminPwd = deobf.decrypt(dataModel.getValue("/admin/ldaprealm/password"));
			setAdminPwd(props, LDAP, adminBindDNName, adminPwd);

			props.put(LDAP + "enableSAML11Assertion", "false");
			props.put(LDAP + "enableSAML20Assertion", "false");

			String initialCtxFctry = dataModel.getValue("/admin/ldaprealm/factory");
			String serverURL = getLDAPUrl(dataModel);
			String searchTimeOut = dataModel.getValue("/admin/ldaprealm/timeout");
			String userSearchBaseDN = dataModel.getValue("/admin/ldaprealm/user/basedn");
			String userSearchExpression = dataModel.getValue("/admin/ldaprealm/user/filter");
			String userAttributeUsersName = dataModel.getValue("/admin/ldaprealm/user/attrname");

			props.put(LDAP + "initialCtxFactory", initialCtxFctry);
			props.put(LDAP + "serverURL", serverURL);

			props.put(LDAP + "userSearchBaseDN", userSearchBaseDN);
			props.put(LDAP + "userSearchExpression", userSearchExpression);
			props.put(LDAP + "userAttributeUsersName", userAttributeUsersName);

			props.put(LDAP + "searchTimeOut", searchTimeOut);

			//ldaprealm2.xform
			String groupIndication = dataModel.getValue("/admin/ldaprealm/group/indication");
			String groupSearchBaseDN = dataModel.getValue("/admin/ldaprealm/group/basedn");
			String groupSearchScopeSubtree = dataModel.getValue("/admin/ldaprealm/group/subtree");
			String groupAttributeUsersName = dataModel.getValue("/admin/ldaprealm/group/groupuserattr");
			String groupAttributeSubgroupsName = dataModel.getValue("/admin/ldaprealm/group/subgroupattr");
			String userSearchScopeSubtree = Boolean.valueOf(groupSearchScopeSubtree).toString();
			String groupAttributeGroupsName = dataModel.getValue("/admin/ldaprealm/group/groupattr");
			String groupSearchExpression = dataModel.getValue("/admin/ldaprealm/group/filter");
			String userAttributeGroupsName = dataModel.getValue("/admin/ldaprealm/group/userAttributeGroupsName");

			String securityAuth = dataModel.getValue("/admin/ldaprealm/authenticationtype");
			
			//props.put(LDAP + "groupIndication", groupIndication); DSS-565 no groupInfo set for AuthN as it's not used in runtime either
			props.put(LDAP + "groupSearchBaseDN", groupSearchBaseDN);
			props.put(LDAP + "groupSearchScopeSubtree", groupSearchScopeSubtree);
			props.put(LDAP + "groupSearchExpression", groupSearchExpression);
			props.put(LDAP + "groupAttributeGroupsName", groupAttributeGroupsName);
			props.put(LDAP + "groupAttributeUsersName", groupAttributeUsersName);
			props.put(LDAP + "groupAttributeSubgroupsName", groupAttributeSubgroupsName);
			props.put(LDAP + "userAttributeGroupsName", dataModel.getValue("/admin/ldaprealm/group/userAttributeGroupsName"));
			props.put(LDAP + "enableNestedGroupSearch", Boolean.valueOf(dataModel.getValue("/admin/ldaprealm/group/enableNestedGroupSearch")).toString());
			props.put(LDAP + "userSearchScopeSubtree", userSearchScopeSubtree);

			props.put(LDAP + "securityAuthentication", securityAuth);
			//props.put(LDAP + "userAttributesExtra", dataModel.getValue("/admin/ldaprealm/user/extraattr"));


			addSSLParams(props, LDAP, dataModel);


			final Configuration cfg = LoginConfigurationFactory.getConfiguration(props);
			CallbackHandler pwdCbh = new UsernamePasswordCallbackHandler(superUser, superPwd.toCharArray());

			loginCtx = new LoginContext(ConnectorUtils.LOOKUP_JVM_PREFIX + ProviderNames.ldap,
					null, pwdCbh, cfg);
			loginCtx.login();
			Subject s = loginCtx.getSubject();

			//authenticated successfully, show attributes to user
			Set<LdapUserPrincipal> principals = s.getPrincipals(LdapUserPrincipal.class);
			LdapUserPrincipal lup = principals.iterator().next();

			props.clear();			
			props.put(LDAPREALM + "serverURL", serverURL);
			props.put(LDAPREALM + "searchTimeOut", searchTimeOut);
			props.put(LDAPREALM + "userSearchBaseDN", userSearchBaseDN);
			props.put(LDAPREALM + "userAttributeUsersName", userAttributeUsersName);
			props.put(LDAPREALM + "groupSearchBaseDN", groupSearchBaseDN);
			props.put(LDAPREALM + "groupSearchScopeSubtree", groupSearchScopeSubtree);
			props.put(LDAPREALM + "groupSearchExpression", groupSearchExpression);
			props.put(LDAPREALM + "userSearchExpression", userSearchExpression);
			props.put(LDAPREALM + "userSearchScopeSubtree", userSearchScopeSubtree);
			props.put(LDAPREALM + "groupAttributeGroupsName", groupAttributeGroupsName);
			props.put(LDAPREALM + "groupIndication", groupIndication);

			props.put(LDAPREALM + "securityAuthentication", securityAuth);
			props.put(LDAPREALM + "initialCtxFactory", initialCtxFctry);
			props.put(LDAPREALM + "groupAttributeSubgroupsName", groupAttributeSubgroupsName);  
			props.put(LDAPREALM + "groupAttributeUsersName", groupAttributeUsersName);
			props.put(LDAPREALM + "userAttributeGroupsName", userAttributeGroupsName);

			setAdminPwd(props, LDAPREALM, adminBindDNName, adminPwd);
			addSSLParams(props, LDAPREALM, dataModel);


			//
			// because of classloading/classpath issues we can't compile directly agains trinity.realm
			// but instead invoke the methods using reflection
			//
//			final LdapRealmProvider connection = new LdapRealmProvider(new LdapConfiguration(true), props);
//			Group[] groups = connection.getAllGroupsforUser(ldapsuperUser)
			
			Class<?> ldapConfigCls = Class.forName("com.tibco.trinity.realm.core.provider.realm.ldap.LdapConfiguration");
			Constructor<?> ctor = ldapConfigCls.getConstructor(Boolean.TYPE);
			Object ldapConfigObj = ctor.newInstance(true);
			
			Class<?> ldapRealmCls = Class.forName("com.tibco.trinity.realm.core.provider.realm.ldap.LdapRealmProvider");
			Constructor<?> ctorRealm = ldapRealmCls.getConstructor(ldapConfigObj.getClass(), Map.class);
			Object ldapRealm = ctorRealm.newInstance(ldapConfigObj, props);
			
			Method realmMthd = ldapRealmCls.getMethod("getAllGroupsForUser", String.class);
			
			
			StringBuffer strGroups = new StringBuffer();
			Object allGroups = realmMthd.invoke(ldapRealm, superUser);
			String belongs;
			int noOfGroups = Array.getLength(allGroups);
			if (noOfGroups > 0) {
				for(int i = 0; i < noOfGroups; i++) {
					strGroups.append(Array.get(allGroups, i));
					strGroups.append(", ");
				}
				strGroups.delete(strGroups.length() - 2, strGroups.length());
				belongs = messageProvider.getMessage("TestLDAPAction.groupInfo", strGroups);
			}
			else
				belongs = messageProvider.getMessage("TestLDAPAction.noGroupsFound");

			String msg = messageProvider.getMessage("TestLDAPAction.userAuthentication",
					superUser, lup.getName(), belongs);

			return msg;
		} catch (Exception e) {
			throw new ActionException("Test LDAP connection failed", e);
		} finally {
			if (loginCtx != null) {
				try {
					loginCtx.logout();
				} catch (Exception e) {
					// do nothing.
				}
			}
		}
	}

	private void addSSLParams(Map<String, Object> props, final String LDAP,
			final IDataModel dataModel) {
		if (Boolean.parseBoolean(dataModel.getValue("/admin/ldaprealm/enablessl"))) {
			props.put(LDAP + "sslIdentityProvider", "class:com.tibco.trinity.runtime.core.provider.identity.trust");
			props.put("com.tibco.trinity.runtime.core.provider.identity.trust.trustStoreServiceProvider",
					"class:com.tibco.trinity.runtime.core.provider.credential.keystore");

			String KS = "com.tibco.trinity.runtime.core.provider.credential.keystore.truststore.";
			props.put(KS + "keyStoreLocation", dataModel.getValue("/admin/ldaprealm/keystorelocation"));
			props.put(KS + "keyStorePassword", dataModel.getValue("/admin/ldaprealm/keystorepassword"));
			props.put(KS + "keyStoreType", dataModel.getValue("/admin/ldaprealm/keystoretype"));
		}
	}

	private void setAdminPwd(Map<String, Object> props, final String LDAP,
			final String adminBindDNName, final String adminPwd) {
		props.put("com.tibco.trinity.runtime.core.provider.credential.password.secretkeystore.protectionParameter",
				"same-pwd");
		props.put("com.tibco.trinity.runtime.core.provider.credential.password.secretkeystore.usernameToken",
				adminBindDNName.replaceAll(",", "\\\\,") + ',' + adminPwd);
		props.put(LDAP + "credentialProvider", "class:com.tibco.trinity.runtime.core.provider.credential.password");
		props.put(LDAP + "keyAlias", adminBindDNName);
		props.put(LDAP + "keyPassword", "same-pwd");
	}

	private String getLDAPUrl(IDataModel dataModel) {
		String protocol = Boolean.parseBoolean(dataModel.getValue("/admin/ldaprealm/enablessl")) ? "ldaps://" : "ldap://";
		String[] hostPorts = dataModel.getValue("/admin/ldaprealm/hostportlist").split(",");
		StringBuffer url = new StringBuffer();
		for (String hostPort : hostPorts) {
			if (url.length() > 0) {
				url.append(' ');
			}
			url.append(protocol);
			url.append(hostPort.trim());
		}
		return url.toString();
	}
	
	private static class UserPasswordDialog extends Dialog implements ModifyListener {
		protected String username;
		protected String password;
		
		public UserPasswordDialog(WizardInstance wizardInstance) throws Exception{
			super(SWTHelper.getShell(wizardInstance));
			
			IDataModel dataModel = wizardInstance.getDataModel();	
			PasswordObfuscator deobf = new PasswordObfuscator();
			username = dataModel.getValue("/admin/authenticationrealm/username");
			password = deobf.decrypt(dataModel.getValue("/admin/authenticationrealm/password"));
		}

		protected Control createDialogArea(final Composite parent) {
			getShell().setText("Enter the target LDAP username and password for testing:");
			Composite composite = (Composite) super.createDialogArea(parent);
			Composite mainContainer = new Composite(composite, SWT.NONE);
			GridData gridData = new GridData(GridData.FILL_BOTH);
			mainContainer.setLayoutData(gridData);
			GridLayout gridLayout = new GridLayout(2, false);
			gridLayout.marginLeft = 5;
			gridLayout.marginRight = 5;
			gridLayout.marginTop = 10;
			mainContainer.setLayout(gridLayout);

			Label label = new Label(mainContainer, SWT.NONE);
			label.setText("User Name: ");
			Text unText = new Text(mainContainer, SWT.BORDER);
			unText.setData("username");
			GridData textGridData = new GridData(GridData.FILL_HORIZONTAL);
			textGridData.widthHint = 260;
			unText.setLayoutData(textGridData);
			unText.setText(username);
			unText.addModifyListener(this);
			
			label = new Label(mainContainer, SWT.NONE);
			label.setText("Password: ");
			Text pwdText = new Text(mainContainer, SWT.BORDER | SWT.PASSWORD);
			pwdText.setData("password");
			textGridData = new GridData(GridData.FILL_HORIZONTAL);
			pwdText.setLayoutData(textGridData);
			pwdText.setText(password);
			pwdText.addModifyListener(this);
			
			return composite;
		}
		
		public void modifyText(ModifyEvent e) {
			Button okButton = getButton(IDialogConstants.OK_ID);
			Text text = (Text) e.widget;
			String id = (String) text.getData();
			if (id.equals("username")) {
				username = text.getText();
				okButton.setEnabled(username.length() != 0);
			} else {
				password = text.getText();
			}
		}
	}
}
