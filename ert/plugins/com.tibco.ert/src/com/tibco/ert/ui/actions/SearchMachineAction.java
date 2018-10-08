package com.tibco.ert.ui.actions;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import com.tibco.amxadministrator.command.line.types.Machine;
import com.tibco.amxadministrator.command.line.typesBase.Enterprise;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.support.IProgressMonitorAware;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.ert.model.core.MachineSearcher;
import com.tibco.ert.model.matrix.AdminServicesDelegate;
import com.tibco.ert.model.utils.ERTUtil;
import com.tibco.ert.ui.customwizard.CWERTUtils;

public class SearchMachineAction implements ICustomAction , IProgressMonitorAware {
	private IProgressMonitor progressMonitor;

	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	/**
	 * search machines from target installation and store the information into a
	 * file
	 */
	public void execute(IActionContext actionContext) throws Exception {
		WizardConfig wizardConfig = actionContext.getWizardConfig();
		IDataModel dataModel = actionContext.getDataModel();

		AdminServicesDelegate delegate = new AdminServicesDelegate(CWERTUtils.createAdminAccessConfig(dataModel,
				"/ert/target"));
		MachineSearcher searcher = new MachineSearcher(delegate);
		searcher.setProgressMonitor(progressMonitor);
		
		progressMonitor.beginTask("Start to search machine", IProgressMonitor.UNKNOWN);
        progressMonitor.subTask("");
        
        Machine[] machines = searcher.search();
        Enterprise enterprise = Enterprise.Factory.newInstance();
        enterprise.setMachineArray(machines);
        
		File tmpFile = new File(
				WizardHelper.getAbsolutePath(wizardConfig.getConfigFile(), "tmp/discoveredMachines.xml"));
		if (!tmpFile.getParentFile().exists()) {
			tmpFile.getParentFile().mkdir();
		}
		ERTUtil.saveEnterprise(tmpFile, enterprise);
		progressMonitor.subTask("Saved to " + tmpFile.getAbsolutePath());
		
		dataModel.setValue("/ert/target/machine-file", tmpFile.getAbsolutePath());
		new UpdateMachineMappingAction().execute(actionContext);
	}
}
