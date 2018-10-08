package com.tibco.ert.ui.actions;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.support.IProgressMonitorAware;
import com.tibco.ert.model.core.EnterpriseExtractor;
import com.tibco.ert.model.core.RepeatExtractor;
import com.tibco.ert.model.matrix.AdminServicesDelegate;
import com.tibco.ert.ui.customwizard.CWERTUtils;
import com.tibco.ert.ui.customwizard.MessageUpdater;

public class ExtractAction implements ICustomAction, IProgressMonitorAware {
	private IProgressMonitor progressMonitor;
	private RepeatExtractor task;

	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public void execute(IActionContext actionContext) throws Exception {
		WizardConfig wizardConfig = actionContext.getWizardConfig();
		IDataModel dataModel = actionContext.getDataModel();
		
		AdminServicesDelegate delegate = new AdminServicesDelegate(CWERTUtils.createAdminAccessConfig(dataModel,
				"/ert/source"));
		EnterpriseExtractor extractor = new EnterpriseExtractor(delegate);

		File extractionDir = new File(dataModel.getValue("/ert/configuration/extraction"));
		String target = dataModel.getValue("/ert/extraction/target");
		boolean repeat = Boolean.parseBoolean(dataModel.getValue("/ert/extraction/repeat"));

		if (repeat) {
			int interval = 10;
			String intervalStr = dataModel.getValue("/ert/extraction/interval");
			if (intervalStr != null && intervalStr.length() > 0) {
				interval = Integer.parseInt(intervalStr);
			}

			if (task != null) {
				task.destroy();
			}
			task = new RepeatExtractor(extractor, interval, target, extractionDir);
			new Thread(task).start();

			MessageUpdater.setMessage(wizardConfig, "Start a background thread to to extract source amx to "
					+ extractionDir.getAbsolutePath());
		} else {
			try {
				extractor.setProgressMonitor(progressMonitor);
				progressMonitor.beginTask("Extraction", 100);
		        progressMonitor.subTask("");
		        
				File dataFile = extractor.extractToZipFile(target, extractionDir);
				dataModel.setValue("/ert/source/datafile", dataFile.getAbsolutePath());
				String message = "Extracted source amx to " + dataFile.getAbsolutePath();
				if (extractor.hasErrors()) {
					message = message + "(with errors, please check log file)";
				}
				MessageUpdater.setMessage(wizardConfig, message);
			} finally {
				extractor.release();
			}
		}
	}
}
