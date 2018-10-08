package com.tibco.configtool.utils;

import java.util.List;

import com.tibco.corona.models.installation.machinemodel.Machine;
import com.tibco.corona.models.installation.machinemodel.TIBCOInstallation;
import com.tibco.devtools.installsupport.commands.prodscripts.ProductScriptsConfig;
import com.tibco.devtools.installsupport.commands.prodscripts.ProductScriptsMetaDataLocator;


public class ProductShellsUtils {
	public static List<ProductScriptsConfig> queryProductConfigList(Machine machine) throws Exception {
		List<TIBCOInstallation> insts = MachineModelUtils.getInstallations(machine);
		ProductScriptsMetaDataLocator locator = new ProductScriptsMetaDataLocator();
		return locator.getResourceConfigs(insts);
	}
}
