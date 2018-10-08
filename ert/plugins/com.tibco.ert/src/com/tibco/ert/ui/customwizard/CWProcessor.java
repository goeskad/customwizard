package com.tibco.ert.ui.customwizard;

import java.io.File;

import com.tibco.customwizard.config.IWizardProcessor;
import com.tibco.customwizard.config.WizardConfig;

public class CWProcessor implements IWizardProcessor {
	public void process(WizardConfig wizardConfig) throws Exception {
		String ertHome = new File(wizardConfig.getConfigFile().getFile()).getParentFile().getAbsolutePath();
		System.setProperty("ert.home", ertHome);
		
		new MainMenuDialog(wizardConfig);
		
		try {
        	wizardConfig.getDataModel().store();
        } catch (Exception e) {
        	wizardConfig.getErrorHandler().handleError(wizardConfig, e);
        }
	}
}
