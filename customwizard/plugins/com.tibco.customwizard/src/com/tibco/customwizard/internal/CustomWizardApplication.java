package com.tibco.customwizard.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.internal.swt.SWTProcessor;
import com.tibco.customwizard.support.WizardApplicationContext;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;

public class CustomWizardApplication implements IApplication {
	public Object start(IApplicationContext context) throws Exception {
		try {
			WizardApplicationContext.setAppArgs((String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS));

			final WizardConfig wizardConfig = WizardHelper.loadDefaultWizardConfig();
			if (wizardConfig.getWizardProcessor() == null) {
				wizardConfig.setWizardProcessor(SWTProcessor.getInstance());
			}
			wizardConfig.getWizardProcessor().process(wizardConfig);
		} catch (Throwable ex) {
			WizardHelper.logException(ex.getMessage(), ex);
			if (isSWTMode()) {
				IStatus status = WizardHelper.createStatus(null, ex);
				SWTHelper.openErrorDialog(ex.getMessage(), status);
			}
		} finally {
			if (Display.getCurrent() != null) {
				Display.getCurrent().dispose();
			}
		}

		return IApplication.EXIT_OK;
	}

	public void stop() {
	}

	protected boolean isSWTMode() {
		return !WizardApplicationContext.hasArg("-consoleMode") && !WizardApplicationContext.hasArg("-silentMode")
				&& !WizardApplicationContext.hasArg("-help");
	}
}
