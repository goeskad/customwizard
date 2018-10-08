package com.tibco.tct.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.ControlUtils;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.devtools.installsupport.commands.prodscripts.ProductScriptsConfig;
import com.tibco.devtools.installsupport.commands.tpclshell.InstantiatedTPCLShellConfig;
import com.tibco.tct.amx.actions.UseDefaultDBAction;
import com.tibco.tct.amx.actions.UseDefaultEMSAction;
import com.tibco.tct.amx.support.AMXContext;
import com.tibco.tct.amx.support.PasswordObfuscator;
import com.tibco.tct.amx.utils.MachineModelUtils;
import com.tibco.tct.amx.utils.TPCLShellsUtils;
import com.tibco.tct.framework.actions.SaveDataModelAction;
import com.tibco.tct.framework.utils.TCTDataModelHelper;
import com.tibco.tct.framework.utils.TCTHelper;

public class AdminSaveDataModelAction extends SaveDataModelAction {
	protected void prepareDataModel(IDataModel dataModel) throws Exception {
		dataModel.setValue("/admin/provisiondrivers/enable", "false");

		Map<String, InstantiatedTPCLShellConfig> provisiondriverMap = new HashMap<String, InstantiatedTPCLShellConfig>();
		dataModel.remove("/admin/provisiondrivers/provisiondriver");
		processDBData(dataModel, provisiondriverMap);
		if (!provisiondriverMap.isEmpty()) {
			dataModel.setValue("/admin/provisiondrivers/enable", "true");
			for (InstantiatedTPCLShellConfig shellConfig : provisiondriverMap.values()) {
				String mode = shellConfig.getProperties().getProperty("provisionMode");
				if (mode != null) {
					dataModel.setValue("/admin/provisiondrivers/provisionMode", mode);
				}
			}
			for (InstantiatedTPCLShellConfig shellConfig : provisiondriverMap.values()) {
				TCTDataModelHelper.addValue(dataModel, "/admin/provisiondrivers/provisiondriver", "");
				TCTDataModelHelper.addValue(dataModel, "/admin/provisiondrivers/provisiondriver/id", TPCLShellsUtils.getOwningRUID(shellConfig).toString());
				TCTDataModelHelper.addValue(dataModel, "/admin/provisiondrivers/provisiondriver/version", TPCLShellsUtils.getOwningRUVersion(shellConfig).toString());
			}
		}

		copyDefaultValueForEMS(dataModel, "/admin/mcrinfo/emsconfig");

		boolean emsEnableSSL = Boolean.parseBoolean(dataModel.getValue("/admin/emsconfig/enablessl"));
		if (emsEnableSSL) {
			dataModel.setValue("/admin/emsconfig/identitypassword", dataModel.getValue("/admin/emsconfig/keystorepassword"));
		} else {
			dataModel.setValue("/admin/emsconfig/identitypassword", new PasswordObfuscator().encrypt(dataModel.getValue("/admin/emsconfig/username")
					+ "_" + TCTHelper.createSessionId()));
		}

		dataModel.setValue("/admin/cli/keystorepassword", new PasswordObfuscator().encrypt(System.currentTimeMillis() + ""));
		dataModel.setValue("/admin/cli/keypassword", new PasswordObfuscator().encrypt(System.currentTimeMillis() + ""));
	}

	protected void processDBData(IDataModel dataModel, Map<String, InstantiatedTPCLShellConfig> provisiondriverMap) throws Exception {
		checkInProcessAdminDB(dataModel);
		setDBDriver(dataModel, "/admin/database", provisiondriverMap);
		copyDefaultValueAndSetDBDriver(dataModel, "/admin/dbrealm", provisiondriverMap);
		copyDefaultValueAndSetDBDriver(dataModel, "/admin/mcrinfo/database", provisiondriverMap);
		copyDefaultValueAndSetDBDriver(dataModel, "/admin/clinfo/logsrvdatabase", provisiondriverMap);
		copyDefaultValueAndSetDBDriver(dataModel, "/admin/clinfo/payloadsrvdatabase", provisiondriverMap);
	}

	private void copyDefaultValueForEMS(IDataModel dataModel, String prefix) throws Exception {
		if (!prefix.equals("/admin/emsconfig") && Boolean.parseBoolean(dataModel.getValue(prefix + "/usedefault"))) {
			UseDefaultEMSAction copyEMS = new UseDefaultEMSAction();
			copyEMS.setCopyFromPrefix("/admin/emsconfig");
			copyEMS.setCopyToPrefix(prefix);
			copyEMS.copyDefault(dataModel);
		}
	}

	private void checkInProcessAdminDB(IDataModel dataModel) {
		if (Boolean.parseBoolean(dataModel.getValue("/admin/database/usedefault"))) {
			UseInProcessDBAction.setInProcessDBData(dataModel);
		}
	}

	private void copyDefaultValueAndSetDBDriver(IDataModel dataModel, String prefix, Map<String, InstantiatedTPCLShellConfig> provisiondriverMap)
			throws Exception {
		if (Boolean.parseBoolean(dataModel.getValue(prefix + "/usedefault"))) {
			UseDefaultDBAction copyDB = new UseDefaultDBAction();
			copyDB.setCopyFromPrefix("/admin/database");
			copyDB.setCopyToPrefix(prefix);
			copyDB.copyDefault(dataModel);
		}
		setDBDriver(dataModel, prefix, provisiondriverMap);
	}

	protected void setDBDriver(IDataModel dataModel, String prefix, Map<String, InstantiatedTPCLShellConfig> provisiondriverMap) throws Exception {
		String shellIdentifier = dataModel.getValue(prefix + "/vendor");
		InstantiatedTPCLShellConfig shellConfig = TPCLShellsUtils.queryInstantiatedTPCLShellConfig(MachineModelUtils.getMachine(),
				TPCLShellsUtils.SHELL_TYPE_JDBC, shellIdentifier);
		if (!TPCLShellsUtils.isHSQL(shellConfig)) {
			provisiondriverMap.put(shellIdentifier, shellConfig);
		}
		String driver = shellConfig.getProperties().getProperty(TPCLShellsUtils.JDBC_DRIVER_NAME);
		String dialect = shellConfig.getProperties().getProperty(TPCLShellsUtils.JDBC_HIBERNATE_DIALECT);
		dataModel.setValue(prefix + "/driver", driver);
		dataModel.setValue(prefix + "/dialect", dialect);
	}

	protected Map<String, Object> createBuildParams(WizardInstance wizardInstance) throws Exception {
		Map<String, Object> params = super.createBuildParams(wizardInstance);
		params.put("amxVersion", AMXContext.getInstance().getAmxVersion());
		setProductParams(wizardInstance, params);
		return params;
	}

	private void setProductParams(WizardInstance wizardInstance, Map<String, Object> params) {
		XForm form = XFormUtils.getXForm(TCTHelper.getSummaryPage(wizardInstance));
		UIControl productGroupControl = AdminSummaryPagePreAction.getProductGroupControl(form.getUI());
		//String securityManager = "true";
		StringBuffer strbuf = new StringBuffer();
		if (productGroupControl != null) {
			List<UIControl> productControlList = ControlUtils.getChildren(productGroupControl);
			Map<UIControl, ProductScriptsConfig> controlProductMap = AdminSummaryPagePreAction.getControlProductMap(wizardInstance);
			for (UIControl productControl : productControlList) {
				ProductScriptsConfig productConfig = controlProductMap.get(productControl);
				if (productConfig != null) {
					if (Boolean.parseBoolean(productControl.getXMLValue())) {
						if (strbuf.length() > 0) {
							strbuf.append(',');
						}
						strbuf.append(productConfig.getName() + "/" + productConfig.getVersion());
						
						// check the securityManager property
//						String securityManagerStr = productConfig.getProperties().getProperty("product.supportsSecurityManager");
//						if (securityManagerStr != null && securityManagerStr.equals("false")) {
//							securityManager = "false";
//						}
					}
				}
			}
		}
		params.put("productList", strbuf.toString());
//		params.put("securityManager", securityManager);
	}
}
