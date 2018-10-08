package com.tibco.ert.ui.actions;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.support.IProgressMonitorAware;
import com.tibco.ert.model.core.BaseERTJob;
import com.tibco.ert.model.core.EnterpriseExtractor;
import com.tibco.ert.model.core.EnterpriseReplicater;
import com.tibco.ert.model.matrix.AdminServicesDelegate;
import com.tibco.ert.ui.customwizard.CWERTUtils;
import com.tibco.ert.ui.customwizard.MessageUpdater;

public class ReplicateAction implements ICustomAction, IProgressMonitorAware {
    private IProgressMonitor progressMonitor;

    public void setProgressMonitor(IProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }

    /**
     * call the external application cli to execute replicating task
     */
    public void execute(IActionContext actionContext) throws Exception {
    	WizardConfig wizardConfig = actionContext.getWizardConfig();
    	IDataModel dataModel = actionContext.getDataModel();
    	
        progressMonitor.beginTask("Replication", 100);
        progressMonitor.subTask("");

		File zipFile = new File(dataModel.getValue("/ert/target/datafile"));
        String strReplicationResult = dataModel.getValue("/ert/replication/extract-result");
        boolean replicationResult = Boolean.parseBoolean(strReplicationResult);

        String target = dataModel.getValue("/ert/extraction/target");
        boolean startNode = Boolean.parseBoolean(dataModel.getValue("/ert/replication/start-node"));
        boolean startSA = Boolean.parseBoolean(dataModel.getValue("/ert/replication/start-sa"));

		AdminServicesDelegate delegate = new AdminServicesDelegate(CWERTUtils.createAdminAccessConfig(dataModel,
				"/ert/target"));
		EnterpriseReplicater replicater = new EnterpriseReplicater(delegate);
		replicater.setProgressMonitor(progressMonitor);

        try {
        	replicater.replicateFromZipFile(target, zipFile, startNode, startSA);
            MessageUpdater.setMessage(wizardConfig, "Replication successful!");
        } finally {
            try {
                if (replicationResult) {
                    EnterpriseExtractor extractor = new EnterpriseExtractor(delegate);
                    extractor.setProgressMonitor(progressMonitor);
                    extractor.updateProgressStatus("Extract the replicate result");
                    File resultFile = extractor.extractToZipFile(BaseERTJob.TARGET_ALL, zipFile.getParentFile());
                    MessageUpdater.setMessage(wizardConfig, "Please check the replication result at " + resultFile.getAbsolutePath());
                }
            } finally {
                replicater.release();
            }
        }
    }
}
