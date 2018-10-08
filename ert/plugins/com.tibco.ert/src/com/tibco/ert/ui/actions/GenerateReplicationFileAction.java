package com.tibco.ert.ui.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.support.IProgressMonitorAware;
import com.tibco.ert.model.core.AdminAccessConfig;
import com.tibco.ert.model.core.ReplicationFileGenerator;
import com.tibco.ert.model.matrix.AdminServicesDelegate;
import com.tibco.ert.ui.customwizard.CWERTUtils;
import com.tibco.ert.ui.customwizard.MessageUpdater;

public class GenerateReplicationFileAction implements ICustomAction, IProgressMonitorAware {
	private IProgressMonitor progressMonitor;

	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	/**
	 * generate machine replication in the source datafile to the target
	 * datafile
	 */
	public void execute(IActionContext actionContext) throws Exception {
		WizardConfig wizardConfig = actionContext.getWizardConfig();
		IDataModel dataModel = actionContext.getDataModel();

		Map<String, String> machineMapping = new HashMap<String, String>();
		for (int i = 1; i < 1000; i++) {
			String sourceMachine = dataModel.getValue("/ert/machine-mapping/source/m" + i);
			if (sourceMachine != null) {
				machineMapping.put(sourceMachine, dataModel.getValue("/ert/machine-mapping/target/m" + i));
			} else {
				break;
			}
		}

		File targetMachineFile = new File(dataModel.getValue("/ert/target/machine-file"));
		final File sourceDataFile = new File(dataModel.getValue("/ert/source/datafile"));
		String replicationDir = dataModel.getValue("/ert/configuration/replication");

		AdminAccessConfig accessConfig = CWERTUtils.createAdminAccessConfig(dataModel, "/ert/target");
		AdminServicesDelegate delegate = new AdminServicesDelegate(accessConfig);

		ReplicationFileGenerator generator = new ReplicationFileGenerator(delegate);
		generator.setProgressMonitor(progressMonitor);
		
		progressMonitor.beginTask("Generate replication file", IProgressMonitor.UNKNOWN);
        progressMonitor.subTask("");
        
		File replicationFile = generator.generate(machineMapping, targetMachineFile, sourceDataFile, replicationDir);

		dataModel.setValue("/ert/target/datafile", replicationFile.getAbsolutePath());

		MessageUpdater.setMessage(wizardConfig,
				"Generated the replication file to " + replicationFile.getAbsolutePath());
	}
}
