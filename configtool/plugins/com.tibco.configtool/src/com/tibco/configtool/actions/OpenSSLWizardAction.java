package com.tibco.configtool.actions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.configtool.utils.URLParser;
import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.ConsoleProcessor;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.trinity.server.credentialserver.common.util.SSLTrustWorkflow;

public class OpenSSLWizardAction implements ICustomAction {
	public static ValidateResult IN_VALID = new ValidateResult(null);
	
	private static final String SSL_ACTION_PROPERTY = "ssl.action";
	
	private static WizardConfig wizardConfig;

	private WizardInstance sourceWizard;
	
	private XForm xform;

	private String serverKeyName;

	private String serverControlId = "url";

	private String serverType = "url";

	private String keyStorePrefix;

	private String host;
	private int port;

	private List<HashMap<String, Object>> caList = new ArrayList<HashMap<String,Object>>();

	public void setServerKeyName(String serverKeyName) {
		this.serverKeyName = serverKeyName;
	}

	public void setServerControlId(String serverControlId) {
		this.serverControlId = serverControlId;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public static OpenSSLWizardAction getOpenSSLWizardAction(WizardInstance wizardInstance) {
		return (OpenSSLWizardAction) wizardInstance.getAttribute(SSL_ACTION_PROPERTY);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public WizardInstance getSourceWizard() {
		return sourceWizard;
	}

	public void setKeyStorePrefix(String keyStorePrefix) {
		this.keyStorePrefix = keyStorePrefix;
	}

	public String getKeyStorePrefix() {
		return keyStorePrefix;
	}

	public List<HashMap<String, Object>> getCaList() {
		return caList;
	}

	public void execute(IActionContext actionContext) throws Exception {
		xform = ((IXFormActionContext) actionContext).getForm();
		sourceWizard = actionContext.getWizardInstance();

		parseHostPort();

		int displayMode = TCTContext.getInstance().getWizardInstance().getDisplayMode();
		WizardInstance wizardInstance = WizardHelper.createWizardInstance(getSSLWizardConfig(), displayMode);
		wizardInstance.setAttribute(SSL_ACTION_PROPERTY, this);
		WizardHelper.setWizardValidateResult(wizardInstance, IN_VALID);

		// [Huabin Zhang, 2011.08.17]:
		// To fix ticket TOOL-994:
		// Try to get CA information before opening the SSL config wizard.
		try {
			List<HashMap<String, Object>> tempCaList = SSLTrustWorkflow.getServerCertDetails(host, port);
			if (tempCaList != null) {
				caList = tempCaList;
			}
		} catch (Exception e) {
			WizardHelper.openErrorDialog(
					actionContext.getWizardInstance(),
					TCTHelper.getMessageProvider(actionContext.getWizardInstance()).getMessage("sslwizard.error.getcertificates", host),
					e);
			return;
		}

		if (WizardHelper.isSWTMode(wizardInstance)) {
			SWTHelper.openWizardDialog(wizardInstance);
		} else if (WizardHelper.isConsoleMode(wizardInstance)) {
			ConsoleProcessor.getInstance().process(wizardInstance);
		}
	}

	private WizardConfig getSSLWizardConfig() throws Exception {
		if (wizardConfig == null) {
			WizardConfig baseWizardConfig = TCTContext.getInstance().getWizardInstance().getWizardConfig();
			wizardConfig = WizardHelper.loadWizardConfig(new URL(baseWizardConfig.getConfigFile(), "sslwizard/WizardConfig.xml"), baseWizardConfig
					.getExtendedClassLoader());
		}
		return wizardConfig;
	}
	
	private void parseHostPort() throws Exception {
		String serverValue;

		if (serverKeyName == null) {
			serverValue = xform.getUI().getControl(serverControlId).getXMLValue();
		} else {
			serverValue = sourceWizard.getDataModel().getValue(serverKeyName);
		}
		if (serverType.equals("url")) {
			Properties props = URLParser.parse(serverValue);
			host = props.getProperty(URLParser.HOST);
			port = Integer.parseInt(props.getProperty(URLParser.PORT));
		} else if (serverType.equals("hostport")) {
			host = xform.getUI().getControl("host").getXMLValue();
			port = Integer.parseInt(xform.getUI().getControl("port").getXMLValue());
		} else {
			serverValue = serverValue.split(",")[0];
			String[] hostport = serverValue.split(":");
			if (hostport.length != 2) {
				throw new ActionException(TCTHelper.getMessageProvider(sourceWizard).getMessage("sslwizard.error.hostportlist"));
			}
			host = hostport[0];
			port = Integer.parseInt(hostport[1]);
		}
	}

}
