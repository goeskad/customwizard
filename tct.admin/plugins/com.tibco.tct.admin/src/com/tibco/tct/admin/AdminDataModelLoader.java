package com.tibco.tct.admin;

import java.util.Map;
import java.util.Properties;

import com.tibco.customwizard.config.IDataModel;
import com.tibco.tct.amx.utils.MachineModelUtils;
import com.tibco.tct.amx.utils.TPCLShellsUtils;
import com.tibco.tct.framework.support.TCTDataModelLoader;
import com.tibco.tct.framework.utils.URLParser;

public class AdminDataModelLoader extends TCTDataModelLoader {
	protected void processChangedProperties(Map<String, String> changedMap, Map<String, String> targetMap, IDataModel currentDataModel) throws Exception {
		for (String key : changedMap.keySet()) {
			// check if the db vendor is configurated
			Map<String, String> identifierMap = TPCLShellsUtils.getInstantiatedShellIdentifierMap(MachineModelUtils.getMachine(), TPCLShellsUtils.SHELL_TYPE_JDBC);
			if (key.endsWith("/vendor")) {
				String vendor = targetMap.get(key);
				if (!identifierMap.containsKey(vendor)) {
					String displayName = TPCLShellsUtils.getShellDisplayName(TPCLShellsUtils.queryTPCLShellConfig(MachineModelUtils.getMachine(), TPCLShellsUtils.SHELL_TYPE_JDBC, vendor));
					throw new Exception("The loading data requires " + displayName
							+ " to be configured, please use the \"Configure Third-Party Driver\" wizard to configure the database driver first.");
				}
			}
			currentDataModel.setValue(key, targetMap.get(key));
		}
	}
	
	protected void processMissedProperties(Map<String, String> missedMap, Map<String, String> targetMap, IDataModel currentDataModel) throws Exception {
		convertLDAPUrlProperty(missedMap, targetMap, currentDataModel);
		super.processMissedProperties(missedMap, targetMap, currentDataModel);
	}

	protected void convertLDAPUrlProperty(Map<String, String> missedMap, Map<String, String> targetMap, IDataModel currentDataModel) throws Exception {
		String convertKey = "/admin/ldaprealm/hostportlist";
		if (missedMap.remove(convertKey) != null) {
			String targetValue = targetMap.get("/admin/ldaprealm/url");
			Properties props = URLParser.parse(targetValue);
			currentDataModel.setValue(convertKey, props.getProperty(URLParser.HOST) + ":" + props.getProperty(URLParser.PORT));
		}
	}
}
