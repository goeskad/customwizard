package com.tibco.configtool.internal;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.MachineModelUtils;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.config.IWizardProcessor;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.SystemConsoleProxy;
import com.tibco.customwizard.support.WizardApplicationContext;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.security.ObfuscationEngine;

public class ConfigToolProcessor implements IWizardProcessor {
	public static Image titleImage;

	public static void preInit() {
		new Thread() {
			public void run() {
				try {
					MachineModelUtils.getMachine();
					ObfuscationEngine.encrypt("init".toCharArray());
					System.out.println("Pre init done");
				} catch (Exception e) {
					WizardHelper.handleError(TCTContext.getInstance().getWizardInstance(), e);
				}
			}
		}.start();
	}

	public void process(WizardConfig wizardConfig) throws Exception {
		TCTContext.initInstance(wizardConfig);
		TCTContext tctContext = TCTContext.getInstance();

		if (tctContext.isConsoleMode()) {
			tctContext.setTibcoConfigHome(tctContext.getTibcoConfigHome());
			tctContext.getWizardInstance().setDisplayMode(WizardInstance.CONSOLE_MODE);
			preInit();
			new TCTConsoleProcessor().process(wizardConfig);
		} else if (tctContext.isSilentMode()) {
			tctContext.setTibcoConfigHome(tctContext.getTibcoConfigHome());
			tctContext.getWizardInstance().setDisplayMode(WizardInstance.CONSOLE_MODE);
			new TCTSilentProcessor().process(wizardConfig);
		} else if (WizardApplicationContext.hasArg("-help")) {
			IMessageProvider messageProvider = TCTHelper.getMessageProvider(tctContext.getWizardInstance());
			SystemConsoleProxy.println(messageProvider.getMessage("tct.help.text"));
		} else {
			tctContext.getWizardInstance().setDisplayMode(WizardInstance.SWT_MODE);
			PlatformUI.createDisplay();
			preInit();

			if (wizardConfig.getIcon() != null) {
				Window.setDefaultImage(SWTHelper.loadImage(wizardConfig, wizardConfig.getIcon()));
			}
			titleImage = SWTHelper.loadImage(wizardConfig, "icons/tct.png");

			int returnCode = new ConfigHomeDialog().open();
			if (returnCode != Window.OK) {
				return;
			}

			new ConfigToolDialog().open();
		}
	}
}
