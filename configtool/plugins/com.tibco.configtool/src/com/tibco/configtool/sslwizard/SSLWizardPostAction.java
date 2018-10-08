package com.tibco.configtool.sslwizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.xforms.ui.UIControl;

import com.tibco.configtool.actions.OpenSSLWizardAction;
import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.security.ObfuscationEngine;
import com.tibco.trinity.server.credentialserver.common.util.SSLTrustWorkflow;

public class SSLWizardPostAction implements ICustomAction {
	public void execute(IActionContext actionContext) throws Exception {
		ArrayList<HashMap<String, Object>> selectedCAList = new ArrayList<HashMap<String, Object>>();
		Map<UIControl, HashMap<String, Object>> controlCAMap = SetCAListAction.getControlCAMap(actionContext.getWizardInstance());

		for (UIControl caControl : controlCAMap.keySet()) {
			if (Boolean.parseBoolean(caControl.getXMLValue())) {
				selectedCAList.add(controlCAMap.get(caControl));
			}
		}

		if (!selectedCAList.isEmpty()) {
			OpenSSLWizardAction sourceAction = OpenSSLWizardAction.getOpenSSLWizardAction(actionContext.getWizardInstance());
			WizardInstance sourceWizard = sourceAction.getSourceWizard();
			IDataModel dataModel = sourceWizard.getDataModel();

			String keystorePassword = ModifyPasswordAction.keystorePassword;
			
			String keyStorePrefix = sourceAction.getKeyStorePrefix();
			String keystoreType = dataModel.getValue(keyStorePrefix + "/keystoretype");
			if (keystoreType == null || keystoreType.length() == 0) {
				keystoreType = "JKS";
			}
			SSLTrustWorkflow.KeyStoreTypeEnum keystoreTypeEnum = SSLTrustWorkflow.KeyStoreTypeEnum.valueOf(keystoreType);

			String keyStoreLocation = TCTContext.getInstance().getKeystorePath() + "/" + keyStorePrefix.substring(1).replace('/', '-') + "-"
					+ TCTHelper.createSessionId() + "." + keystoreType.toLowerCase();

			SSLTrustWorkflow.saveCertsToKeyStoreFile(sourceAction.getHost(), sourceAction.getPort(), selectedCAList, keyStoreLocation,
					keystorePassword.toCharArray(), keystoreTypeEnum, true, null);

			dataModel.setValue(keyStorePrefix + "/keystorelocation", keyStoreLocation);
			dataModel.setValue(keyStorePrefix + "/keystorepassword", ObfuscationEngine.encrypt(keystorePassword.toCharArray()));
			dataModel.setValue(keyStorePrefix + "/keystoretype", keystoreType);
			
			XFormUtils.getXForm(sourceWizard.getCurrentPage()).reset();
			TCTHelper.revalidateCurrentPage(sourceWizard);
		}
	}
}
