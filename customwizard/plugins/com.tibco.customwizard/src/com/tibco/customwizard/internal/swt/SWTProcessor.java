package com.tibco.customwizard.internal.swt;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.tibco.customwizard.config.IWizardProcessor;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;

public class SWTProcessor implements IWizardProcessor {
	private static SWTProcessor instance = new SWTProcessor();

	public static SWTProcessor getInstance() {
		return instance;
	}

	private SWTProcessor() {
	}

	public void process(WizardConfig wizardConfig) throws Exception {
		if (Display.getCurrent() == null) {
			PlatformUI.createDisplay();
		}
		WizardInstance wizardInstance = WizardHelper.createWizardInstance(wizardConfig, WizardInstance.SWT_MODE);
		SWTHelper.openWizardDialog(wizardInstance);
	}
	
	
}
