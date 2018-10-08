package com.tibco.configtool.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;

import com.tibco.configtool.support.ICleanupableAction;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.instance.WizardInstance;

public class CLIAction extends TCTPostAction implements ICleanupableAction {
	private String logFileName;
	private String targetName;

	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public void execute(IActionContext actionContext) throws Exception {
		progressMonitor.beginTask(description, IProgressMonitor.UNKNOWN);

		execute(actionContext.getWizardInstance(), getLogFileName(), getTargetName(), progressMonitor);
	}

	public static void execute(WizardInstance wizardInstance, String logFileName, String targetName, IProgressMonitor progressMonitor) throws Exception {
		File sessionDir = SaveDataModelAction.getSaveDataModelAction(wizardInstance).getSessionDir();

		File logDir = new File(sessionDir, "logs");
		File logFile = new File(logDir, logFileName);
		logFile.getParentFile().mkdirs();
		try {
			File scriptsDir = new File(sessionDir, SaveDataModelAction.SCRIPTS_FOLDER);
			TCTHelper.callAnt(new File(scriptsDir, "build.xml"), targetName, null, wizardInstance, logFile, progressMonitor);
		} catch (Exception e) {
			Throwable cause = e;
			if (e instanceof InvocationTargetException) {
				if (e.getCause() != null) {
					cause = e.getCause();
				}
			}
			throw new ActionException(TCTHelper.getMessageProvider(wizardInstance).getMessage("CLIAction.error"), cause);
		}
	}

	public void executeCleanup(IActionContext actionContext) throws Exception {

	}
}
