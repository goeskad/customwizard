package com.tibco.configtool.support;

import java.io.File;

import com.tibco.configtool.actions.CLIAction;
import com.tibco.configtool.actions.SaveDataModelAction;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.IWizardProcessor;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.WizardHelper;

public class SilentWizardProcessor implements IWizardProcessor {
	public void process(WizardConfig wizardConfig) throws Exception {
		WizardInstance wizardInstance = new WizardInstance(wizardConfig);
		IDataModel dataModel = WizardHelper.createDataModel(wizardConfig);
		wizardInstance.setDataModel(dataModel);
		
		SaveDataModelAction action = createSaveDataModelAction();
		action.setContributoName(getContributorName());
		action.markSelf(wizardInstance);
		
		action.processResFiles(null, wizardInstance);
		// copy the contributor build.properties to scripts folder
		File contributorProps = new File(TCTContext.getInstance().getSilentDataFile());
		TCTHelper.copyFile(contributorProps, new File(action.getSessionDir(), SaveDataModelAction.SCRIPTS_FOLDER + "/build.properties"));

		CLIAction.execute(wizardInstance, "tct.silent.log", TCTContext.getInstance().getSilentTargetName(), null);
	}

	protected SaveDataModelAction createSaveDataModelAction() {
		return new SaveDataModelAction();
	}

	protected String getContributorName() {
		String id = TCTContext.getInstance().getSilentContributorId();
		int index = id.indexOf(".tct.");
		if (index > 0) {
			return id.substring(index + 5).replace(".", "");
		}
		return id;
	}
}
