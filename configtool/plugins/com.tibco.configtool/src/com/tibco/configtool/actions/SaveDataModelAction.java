package com.tibco.configtool.actions;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.WizardHelper;

public class SaveDataModelAction implements ICustomAction {
	public static final String SAVE_MODEL_DATA_FOLDER = "private";

	public static final String SCRIPTS_FOLDER = "scripts";
	
	public static final String TEMPLATE_FOLDER = "templates";
	
	private static final String SAVE_MODEL_ACTION = "model.action";

	private String contributoName;

	private String sessionId = TCTHelper.createSessionId();

	private File sessionDir;

	public String getContributoName() {
		return contributoName;
	}

	public void setContributoName(String contributoName) {
		this.contributoName = contributoName;
		sessionDir = new File(TCTContext.getInstance().getWorkDir(), contributoName + TCTHelper.fileSeparator
				+ sessionId);
	}

	public File getSessionDir() {
		return sessionDir;
	}

	public void setSessionDir(File sessionDir) {
		this.sessionDir = sessionDir;
	}

	public static SaveDataModelAction getSaveDataModelAction(WizardInstance wizardInstance) {
		return (SaveDataModelAction) wizardInstance.getAttribute(SAVE_MODEL_ACTION);
	}

	public void execute(IActionContext actionContext) throws Exception {
		WizardInstance wizardInstance = actionContext.getWizardInstance();

		TCTHelper.removeDataModelFile(wizardInstance);
		markSelf(wizardInstance);
	}

	public void markSelf(WizardInstance wizardInstance) {
		wizardInstance.setAttribute(SAVE_MODEL_ACTION, this);
	}

	public File saveDataModel(WizardInstance wizardInstance) throws Exception {
		File dataDir = new File(sessionDir, SAVE_MODEL_DATA_FOLDER);
		dataDir.mkdirs();

		IDataModel dataModel = wizardInstance.getDataModel();
		
		prepareDataModel(dataModel);
		File dataModelFile = TCTHelper.storeDataModel(dataModel, dataDir);
		processResFiles(dataModelFile, wizardInstance);
		return dataModelFile;
	}

	protected void prepareDataModel(IDataModel dataModel) throws Exception {
	}

	public void processResFiles(File dataModelFile, WizardInstance wizardInstance)
			throws Exception {
		File scriptsDir = new File(sessionDir, SCRIPTS_FOLDER);
		scriptsDir.mkdirs();
		File templateDir = new File(scriptsDir, TEMPLATE_FOLDER);
		templateDir.mkdir();
		
		File[] resFiles = getResFiles(wizardInstance);
		for (File file : resFiles) {
			if (file.getName().equals("build.xsl")) {
				Map<String, Object> params = createBuildParams(wizardInstance);
				if (dataModelFile != null) {
					File output = new File(scriptsDir, "build.properties");
					TCTHelper.processXSL(dataModelFile, file, output, params);
				}
			} else if (file.getName().equals("build.xml")) {
				File output = new File(scriptsDir, file.getName());
				TCTHelper.copyFile(file, output);
			} else if (!file.isDirectory() && !file.getName().equals("config.properties")) {
				File output = new File(templateDir, file.getName());
				TCTHelper.copyFile(file, output);
			}
		}
		
		//copy tct-tools.xml
		URL configFile = TCTContext.getInstance().getWizardInstance().getWizardConfig().getConfigFile();
		File source = new File(WizardHelper.getAbsolutePath(configFile, "res/tct-tools.xml"));
		File output = new File(templateDir, source.getName());
		TCTHelper.copyFile(source, output);
	}

	protected File[] getResFiles(WizardInstance wizardInstance) {
		URL configFile = wizardInstance.getWizardConfig().getConfigFile();
		File resFolder = new File(WizardHelper.getAbsolutePath(configFile, "res"));
		if (resFolder.isDirectory()) {
			return resFolder.listFiles();
		}
		return new File[0];
	}

	protected Map<String, Object> createBuildParams(WizardInstance wizardInstance)
			throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tibcoHome", TCTContext.getInstance().getTibcoHome());
		params.put("tibcoConfigHome", TCTContext.getInstance().getTibcoConfigHome());
		params.put("amxVersion", TCTContext.getInstance().getAmxVersion());

		return params;
	}
}
