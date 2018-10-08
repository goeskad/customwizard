package com.tibco.tct.admin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.Set;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.ControlUtils;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.tct.framework.utils.TCTHelper;
import com.tibco.trinity.server.credentialserver.common.util.KeystoreAutodetectHelper;

public class FetchKeystoreAction implements ICustomAction {

	/**
	 * detect type and get list for current keysotre file.
	 */
	public void execute(IActionContext actionContext) {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		WizardInstance wizardInstance = actionContext.getWizardInstance();
		XForm form = xformActionContext.getForm();
		String keyStoreLocation = form.getUI().getControl("keystorelocation").getXMLValue();
		String keyStorePassword = form.getUI().getControl("keystorepassword").getXMLValue();
		UIControl aliasControl = form.getUI().getControl("keyalias");
		String keyType;

		if ("".equals(keyStoreLocation) || "".equals(keyStorePassword)) {
			WizardHelper.openMessage(wizardInstance, "Please enter keyStore file Loacation and Password");
			return;
		}

		try {
			keyType = getKeyStoreType(keyStoreLocation, keyStorePassword);

			if ("JKS".equals(keyType) || "JCEKS".equals(keyType)) {
				showAliasList(form, keyType);
			} else if ("PKCS12".equals(keyType)) {
				showAliasList(form, keyType, keyStorePassword);
			}

			Set<String> aliasNames = new HashSet<String>();
			for (String tmp : getKeyStoreAliasList(keyStoreLocation, keyStorePassword, keyType)) {
				aliasNames.add(tmp);
			}
			TCTHelper.updateCombo(aliasControl, aliasNames);
			TCTHelper.revalidateCurrentPage(actionContext.getWizardInstance());

		} catch (KeyStoreException e) {
			WizardHelper.openMessage(wizardInstance, "Wrong password ! ");
		} catch (FileNotFoundException e) {
			WizardHelper.openErrorDialog(wizardInstance, e);
		} catch (IOException e) {
			WizardHelper.openErrorDialog(wizardInstance, e);
		} catch (NoSuchAlgorithmException e) {
			WizardHelper.openErrorDialog(wizardInstance, e);
		} catch (CertificateException e) {
			WizardHelper.openErrorDialog(wizardInstance, e);
		} catch (NoSuchProviderException e) {
			WizardHelper.openErrorDialog(wizardInstance, e);
		}
	}

	/**
	 * show list of alias and password and set keystore type.
	 */
	private void showAliasList(XForm form, String keyType) {
		UIControl control = form.getUI().getControl("aliasgroup");
		ControlUtils.setVisible(control, true);
		control.setEnabled(true);
		form.getUI().getControl("keystoretype").setXMLValue(keyType);
	}

	/**
	 * show list of alias and password and set keystore type/password since P12
	 * type will use same password as keystore.
	 */
	private void showAliasList(XForm form, String keyType, String password) {
		UIControl control = form.getUI().getControl("aliasgroup");
		ControlUtils.setVisible(control, true);
		control.setEnabled(true);
		form.getUI().getControl("keystoretype").setXMLValue(keyType);
		form.getUI().getControl("keypassword").setXMLValue(password);
	}

	private String getKeyStoreType(String filePath, String pwd) throws KeyStoreException, FileNotFoundException,
			IOException {
		return KeystoreAutodetectHelper.autodetectFormat(new FileInputStream(filePath), pwd.toCharArray(), null);
	}

	private String[] getKeyStoreAliasList(String filePath, String pwd, String type) throws KeyStoreException,
			FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException {
		return KeystoreAutodetectHelper.listAliases(new FileInputStream(filePath), pwd.toCharArray(), type, null);
	}
}
