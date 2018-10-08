package com.tibco.configtool.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.xforms.ui.UIForm;

import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.PropertyUtils;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class KeyStoreValidator implements ICustomAction {
	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		UIForm form = xformActionContext.getForm().getUI();

		try {
			verifyKeystore(form);
			WizardInstance wizardInstance = actionContext.getWizardInstance();
			String message = TCTHelper.getMessageProvider(wizardInstance).getMessage("KeyStoreValidator.success");
			WizardHelper.openMessage(wizardInstance, message);
		} catch (Exception e) {
			WizardHelper.openErrorDialog(actionContext.getWizardInstance(), e);
		}
	}

	protected void verifyKeystore(UIForm form) throws Exception {
		Map<Object, Object> configProps = new HashMap<Object, Object>();
		configProps.put("hostName", "locahost");
		configProps.put("portNo", "8090");
		configProps.put("keyStoreLocation", form.getControl("keystorelocation").getXMLValue());
		configProps.put("keyStoreType", form.getControl("keystoretype").getXMLValue());
		configProps.put("keyStorePassword", form.getControl("keystorepassword").getXMLValue());
		configProps.put("keyAlias", form.getControl("keyalias").getXMLValue());
		configProps.put("keyPassword", form.getControl("keypassword").getXMLValue());

		Class<?> configClass = Class
				.forName("com.tibco.trinity.server.credentialserver.ca.configuration.CAConfiguration");
		Object configObject = configClass.newInstance();
		PropertyUtils.setAttributes(configObject, configProps);
		Class<?> utilClass = Class
				.forName("com.tibco.trinity.server.credentialserver.ca.configuration.impl.CAConfigurationUtil");
		try {
			utilClass.getMethod("validate", configClass).invoke(utilClass.newInstance(), configObject);
		} catch (InvocationTargetException e) {
			throw (Exception) e.getCause();
		}
	}
}
