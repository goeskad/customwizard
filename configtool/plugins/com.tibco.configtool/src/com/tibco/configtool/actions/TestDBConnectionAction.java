package com.tibco.configtool.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.MachineModelUtils;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.configtool.utils.TPCLShellsUtils;
import com.tibco.configtool.utils.URLParser;
import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.IBackgroudAction;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.devtools.installsupport.commands.tpclshell.InstantiatedTPCLShellConfig;

public class TestDBConnectionAction implements ICustomAction, IBackgroudAction {
	private static Method dbConnectionTester;

	private IMessageProvider messageProvider;
	
	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		WizardInstance wizardInstance = actionContext.getWizardInstance();
		
		messageProvider = TCTHelper.getMessageProvider(wizardInstance);
		
		String vendorIdentifier = getVendorIdentifier(actionContext, form);
		if (vendorIdentifier == null || vendorIdentifier.length() == 0) {
			WizardHelper.openErrorMessage(wizardInstance, messageProvider.getMessage("TestDBConnectionAction.error.nondriver"));
			return;
		}
		String url = form.getUI().getControl("url").getXMLValue();
		String username = form.getUI().getControl("username").getXMLValue();
		String password = form.getUI().getControl("password").getXMLValue();

		Properties urlProps;
		try {
			urlProps = URLParser.parse(url);
		} catch (Exception e) {
			WizardHelper.openErrorDialog(wizardInstance, e);
			return;
		}
		
		boolean createTable = false;
		UIControl createTableCheckbox = form.getUI().getControl("createtable");
		if (createTableCheckbox != null) {
			createTable = Boolean.parseBoolean(createTableCheckbox.getXMLValue());
		}
		InstantiatedTPCLShellConfig shellConfig = TPCLShellsUtils.queryInstantiatedTPCLShellConfig(MachineModelUtils.getMachine(),
				TPCLShellsUtils.SHELL_TYPE_JDBC, vendorIdentifier);

		String driver = shellConfig.getProperties().getProperty(TPCLShellsUtils.JDBC_DRIVER_NAME);
		try {
			Properties info = new Properties();
			info.put("user", username);
			info.put("password", password);
			packSSLProperties(info, urlProps.getProperty(URLParser.TYPE), form);

			testDBConnection(driver, url, info, createTable);
			WizardHelper.openMessage(wizardInstance, messageProvider.getMessage("testconnection.success"));
		} catch (Exception e) {
			WizardHelper.openErrorDialog(wizardInstance, e);
		}
	}

	protected String getVendorIdentifier(IActionContext actionContext, XForm form) {
		return form.getUI().getControl("vendor").getXMLValue();
	}

	private void packSSLProperties(Properties info, String dbType, XForm form) throws Exception {
		if (form.getUI().getControl("sslcontrol") == null) {
			return;
		}
		
		boolean enablessl = Boolean.parseBoolean(form.getUI().getControl("sslcontrol").getXMLValue());
		if (enablessl) {
			String location = form.getUI().getControl("keystorelocation").getXMLValue();
			String type = form.getUI().getControl("keystoretype").getXMLValue();
			String password = form.getUI().getControl("keystorepassword").getXMLValue();
			if (dbType == URLParser.MYSQL) {
				info.put("useSSL", "true");
				info.put("trustCertificateKeyStoreUrl", TCTHelper.toURL(new File(location)).toString());
				info.put("trustCertificateKeyStoreType", type);
				info.put("trustCertificateKeyStorePassword", password);
			} else if (dbType == URLParser.MSSQL) {
				info.put("encrypt", "true");
				info.put("trustStore", location);
				info.put("trustStorePassword", password);
			} else if (dbType == URLParser.ORACLE) {
				info.put("javax.net.ssl.trustStore", location);
				info.put("javax.net.ssl.trustStoreType", type);
				info.put("javax.net.ssl.trustStorePassword", password);
			} else if (dbType == URLParser.DB2) {
				info.put("sslTrustStoreLocation", location);
				info.put("sslTrustStorePassword", password);
				info.put("sslConnection", "true");
			}
		}
	}
	
	public boolean testDBConnection(String driver, String url, Properties info, boolean createTable) throws Exception {
		if (dbConnectionTester == null) {
			List<URL> urlList = TPCLShellsUtils.getShellClasspathUrls(MachineModelUtils.getMachine(), TPCLShellsUtils.SHELL_TYPE_JDBC);
			urlList.add(TCTContext.getInstance().getTctJarUrl());
			ClassLoader dbDriverClassLoader = new URLClassLoader(urlList.toArray(new URL[urlList.size()]));
			Class<?> testerClass = dbDriverClassLoader.loadClass("com.tibco.configtool.internal.support.DBConnectionTester");
			dbConnectionTester = testerClass.getMethod("testDBConnection",
					new Class[] { String.class, String.class, Properties.class, boolean.class });
		}

		try {
			dbConnectionTester.invoke(null, driver, url, info, createTable);
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				e = (Exception) e.getCause();
			}
			String errorMsg = e.getMessage();
			if (e instanceof ClassNotFoundException) {
				errorMsg = e.toString();
			}
			throw new ActionException("Could not establish connection to the database: " + errorMsg, e);
		}

		return true;
	}
	
	public static void resetDBDrivers() {
		dbConnectionTester = null;
	}
}
