package com.tibco.configtool.actions;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.utils.SSLUtils;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.IBackgroudAction;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.trinity.runtime.core.provider.identity.trust.TrustRuntimeException;

public class TestAdminConnectionAction implements ICustomAction, IBackgroudAction {
	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		WizardInstance wizardInstance = actionContext.getWizardInstance();
		
		IMessageProvider messageProvider = TCTHelper.getMessageProvider(wizardInstance);

		String host = form.getUI().getControl("host").getXMLValue();
		String port = form.getUI().getControl("port").getXMLValue();
		String username = form.getUI().getControl("username").getXMLValue();
		String password = form.getUI().getControl("password").getXMLValue();

		boolean sslEnabled = Boolean.parseBoolean(form.getUI().getControl("sslcontrol").getXMLValue());

		String adminURL = "http" + (sslEnabled ? "s" : "") + "://" + host + ":" + port 
		                + "/amxadministrator/j_security_check?j_username="
		                + URLEncoder.encode(username, "UTF-8")
                        + "&j_password=" + URLEncoder.encode(password, "UTF-8");
		URL url = new URL(adminURL);

		if (sslEnabled) {
			try {
				String keystoreLocation = form.getUI().getControl("keystorelocation").getXMLValue();
				String keystorePassword = form.getUI().getControl("keystorepassword").getXMLValue();
				String keystoreType = form.getUI().getControl("keystoretype").getXMLValue();
				SSLUtils.setDefaultHttps(keystoreLocation, keystorePassword, keystoreType);
			} catch (TrustRuntimeException e) {
				WizardHelper.openErrorDialog(wizardInstance, messageProvider.getMessage("testconnection.error.keystore"), e);
				return;
			}
		}

		InputStream in = null;
		try {
			URLConnection urlConnection = url.openConnection();
			urlConnection.setUseCaches(false);
			in = urlConnection.getInputStream();
		} catch (Exception e) {
			WizardHelper.openErrorDialog(wizardInstance, messageProvider.getMessage("TestAdminConnectionAction.error.connect"), e);
			return;
		}

		String response = new String(TCTHelper.readStream(in));

		if (!response.contains("function redirect(){")) {
			WizardHelper.openErrorMessage(wizardInstance, messageProvider.getMessage("TestAdminConnectionAction.error.logon"));
			return;
		}

		WizardHelper.openMessage(wizardInstance, messageProvider.getMessage("testconnection.success"));
	}
}
