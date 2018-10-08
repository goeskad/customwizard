package com.tibco.customwizard.util;

import java.io.File;

import com.tibco.customwizard.config.DataModelConfig;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.IDataModelLoader;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.XMLDataModelLoader;

public class DataModelHelper {
	public static IDataModel createDataModel(WizardConfig wizardConfig) throws Exception {
		DataModelConfig dataModelConfig = wizardConfig.getDataModelConfig();
		if (dataModelConfig != null) {
			File file = WizardHelper.getResourceFile(wizardConfig, dataModelConfig.getFile());
			return createDataModel(wizardConfig, file);
		}
		return null;
	}

	public static IDataModel createDataModel(WizardConfig wizardConfig, File file) throws Exception {
		return getDataModelLoader(wizardConfig).createDataModel(file);
	}
	
	public static void loadDataModel(WizardInstance wizardInstance, File file) throws Exception {
		IDataModelLoader dataModelLoader = getDataModelLoader(wizardInstance.getWizardConfig());
		dataModelLoader.loadDataModel(wizardInstance.getDataModel(), dataModelLoader.createDataModel(file));
	}

	public static void saveDataModel(WizardInstance wizardInstance) throws Exception {
		DataModelConfig dataModelConfig = wizardInstance.getWizardConfig().getDataModelConfig();
		if (dataModelConfig != null) {
			File file = WizardHelper.getResourceFile(wizardInstance.getWizardConfig(), dataModelConfig.getFile());
			saveDataModel(wizardInstance, file);
		}
	}
	
	public static void saveDataModel(WizardInstance wizardInstance, File file) throws Exception {
		getDataModelLoader(wizardInstance.getWizardConfig()).saveDataModel(wizardInstance.getDataModel(), file);
	}
	
	public static IDataModelLoader getDataModelLoader(WizardConfig wizardConfig) {
		DataModelConfig dataModelConfig = wizardConfig.getDataModelConfig();
		if (dataModelConfig != null && dataModelConfig.getLoader() != null) {
			return dataModelConfig.getLoader();
		} else if (wizardConfig.getParentWizardConfig() != null) {
			return getDataModelLoader(wizardConfig.getParentWizardConfig());
		}
		return new XMLDataModelLoader();
	}
}
