package com.tibco.configtool.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.Manifest;

import com.tibco.corona.models.installation.machinemodel.Machine;
import com.tibco.corona.models.installation.machinemodel.ReleaseUnit;
import com.tibco.corona.models.installation.machinemodel.ReleaseUnitMember;
import com.tibco.corona.models.installation.machinemodel.TIBCOInstallation;
import com.tibco.devtools.installsupport.commands.tpclshell.InstantiatedTPCLShellConfig;
import com.tibco.devtools.installsupport.commands.tpclshell.InstantiatedTPCLShellMetaDataLocator;
import com.tibco.devtools.installsupport.commands.tpclshell.TPCLShellConfig;
import com.tibco.devtools.installsupport.commands.tpclshell.TPCLShellMetaDataLocator;
import com.tibco.neo.model.types.ComponentID;
import com.tibco.neo.model.types.VersionNumber;

public class TPCLShellsUtils {
	public static final String SHELL_TYPE_JDBC = "jdbc";

	public static final String JDBC_URL_PATTERN = "jdbc.url.pattern";

	public static final String JDBC_DEFAULT_URL = "jdbc.default.url";

	public static final String JDBC_DRIVER_NAME = "jdbc.driver.name";
	
	public static final String JDBC_XADRIVER_NAME = "jdbc.data.source.class";
	
	public static final String JDBC_HIBERNATE_DIALECT = "hibernate.dialect";
	
	public static final String RU_ID_PROP = "shell.ru.id"; 
	
	public static final String RU_DISPLAY_NAME_PROP = "shell.ru.name"; //display name
	
	public static final String HSQL_RU_ID = "com.tibco.tpcl.org.hsqldb.feature";
	
	public static List<TPCLShellConfig> queryTPCLShellConfig(Machine machine, String shellType) throws Exception {
		List<TIBCOInstallation> insts = MachineModelUtils.getInstallations(machine);
		TPCLShellMetaDataLocator locator;
		if (shellType == null) {
			locator = new TPCLShellMetaDataLocator();
		} else {
			locator = new TPCLShellMetaDataLocator(shellType);
		}
		return locator.getResourceConfigs(insts);
	}

	public static List<InstantiatedTPCLShellConfig> queryInstantiatedTPCLShellConfig(Machine machine, String shellType)
			throws Exception {
		List<TIBCOInstallation> insts = MachineModelUtils.getInstallations(machine);
		InstantiatedTPCLShellMetaDataLocator locator;
		if (shellType == null) {
			locator = new InstantiatedTPCLShellMetaDataLocator();
		} else {
			locator = new InstantiatedTPCLShellMetaDataLocator(shellType);
		}
		return locator.getResourceConfigs(insts);
	}

	public static TPCLShellConfig queryTPCLShellConfig(Machine machine, String shellType, String shellIdentifier)
			throws Exception {
		List<TPCLShellConfig> shellConfigList = queryTPCLShellConfig(machine, shellType);
		TPCLShellConfig matched = null;
		for (TPCLShellConfig shellConfig : shellConfigList) {
			if (getShellIdentifier(shellConfig).equals(shellIdentifier)) {
				if (matched == null || shellConfig.getOwningRUMemberVersion().compareTo(matched.getOwningRUMemberVersion()) > 0) {
					matched = shellConfig;
				}
			}
		}
		return matched;
	}

	public static String getShellJars(TPCLShellConfig shellConfig) throws Exception {
		Manifest manifestProps = new Manifest(shellConfig.getShellManifestStream());
		String classPath = manifestProps.getMainAttributes().getValue("Bundle-ClassPath");

		return classPath;
	}
	
	public static InstantiatedTPCLShellConfig queryInstantiatedTPCLShellConfig(Machine machine, String shellType,
			String shellIdentifier) throws Exception {
		List<InstantiatedTPCLShellConfig> shellConfigList = queryInstantiatedTPCLShellConfig(machine, shellType);
		InstantiatedTPCLShellConfig matched = null;
		for (InstantiatedTPCLShellConfig shellConfig : shellConfigList) {
			if (getShellIdentifier(shellConfig).equals(shellIdentifier)) {
				if (matched == null || shellConfig.getOwningRUMemberVersion().compareTo(matched.getOwningRUMemberVersion()) > 0) {
					matched = shellConfig;
				}
			}
		}
		return matched;
	}

	public static Set<String> getShellTypeSet(Machine machine) throws Exception {
		List<TPCLShellConfig> shellConfigList = queryTPCLShellConfig(machine, null);
		Set<String> shellTypeList = new HashSet<String>();
		for (TPCLShellConfig shellConfig : shellConfigList) {
			shellTypeList.add(shellConfig.getType());
		}
		return shellTypeList;
	}
    /**
     * 
     * @param machine
     * @param shellType i.e. jdbc
     * @return get id/display_name map 
     * @throws Exception
     */
	public static Map<String,String> getShellIdentifierMap(Machine machine, String shellType) throws Exception {
		List<TPCLShellConfig> shellConfigList = TPCLShellsUtils.queryTPCLShellConfig(machine, shellType);
		Map<String,String> identifierMap = new TreeMap<String,String>();
		for (TPCLShellConfig shellConfig : shellConfigList) {
			identifierMap.put(getShellIdentifier(shellConfig),getShellDisplayName(shellConfig));
		}
		return identifierMap;
	}
	 /**
     * 
     * @param machine
     * @param InstantiatedShellType i.e. jdbc
     * @return get id/display_name map 
     * @throws Exception
     */
	public static Map<String,String> getInstantiatedShellIdentifierMap(Machine machine, String instantiatedShellType) throws Exception {
		List<InstantiatedTPCLShellConfig> shellConfigList = TPCLShellsUtils.queryInstantiatedTPCLShellConfig(machine,
				instantiatedShellType);
		Map<String,String> identifierMap = new HashMap<String,String>();
		for (InstantiatedTPCLShellConfig instantiatedShellConfig : shellConfigList) {
			identifierMap.put(getShellIdentifier(instantiatedShellConfig),getShellDisplayName(instantiatedShellConfig));
		}
		return identifierMap;
	}
    
    /**
     * 
     * @param instantiatedShellConfig
     * @return Identifier by id
     */
	public static String getShellIdentifier(InstantiatedTPCLShellConfig instantiatedShellConfig) {
		VersionNumber version = instantiatedShellConfig.getVersion();
		String ruId = instantiatedShellConfig.getProperties().getProperty(RU_ID_PROP);
		ruId = ruId == null ? getOwningRUID(instantiatedShellConfig).toString() : ruId;

		return ruId + " " + version.getMajor() + "." + version.getMinor() + "." + version.getServicePack();
	}

	/**
	 * @param shellConfig
	 * @return Identifier by id
	 */
	public static String getShellIdentifier(TPCLShellConfig shellConfig) {
		VersionNumber version = shellConfig.getVersion();
		return shellConfig.getRUID() + " " + version.getMajor() + "." + version.getMinor() + "."
				+ version.getServicePack();
	}
	
	 /**
     * 
     * @param instantiatedShellConfig
     * @return display name
     */
	public static String getShellDisplayName(InstantiatedTPCLShellConfig instantiatedShellConfig) {
		VersionNumber version = instantiatedShellConfig.getVersion();
		String displayName = instantiatedShellConfig.getProperties().getProperty(RU_DISPLAY_NAME_PROP);
		displayName= displayName == null ? instantiatedShellConfig.getProperties().getProperty("shell.vendor.name") : displayName;
		
		return displayName + " " + version.getMajor() + "."
				+ version.getMinor() + "." + version.getServicePack();
	}

	/**
	 * @param shellConfig
	 * @return display name
	 */
	public static String getShellDisplayName(TPCLShellConfig shellConfig) {
		VersionNumber version = shellConfig.getVersion();
		return shellConfig.getRUDisplayName() + " " + version.getMajor() + "." + version.getMinor() + "."
				+ version.getServicePack();
	}

	@SuppressWarnings("unchecked")
	public static List<URL> getShellClasspathUrls(Machine machine, String shellType) throws Exception {
		List<URL> urlList = new ArrayList<URL>();
//		urlList.add(TCTContext.getInstance().getTctJarUrl());

		List<InstantiatedTPCLShellConfig> shellConfigList = TPCLShellsUtils.queryInstantiatedTPCLShellConfig(machine,
				shellType);
		for (InstantiatedTPCLShellConfig shellConfig : shellConfigList) {
			ReleaseUnit releaseUnit = MachineModelUtils.getReleaseUnit(machine, getOwningRUID(shellConfig),
					getOwningRUVersion(shellConfig));
			List<ReleaseUnitMember> memberBundles = releaseUnit.getMemberBundles();
			ComponentID owningRUMemberID = getOwningRUMemberID(shellConfig);
			VersionNumber owningRUMemberVersion = getOwningRUMemberVersion(shellConfig);
			for (ReleaseUnitMember memberBundle : memberBundles) {
				if (memberBundle.getComponentID().equals(owningRUMemberID)
						&& memberBundle.getVersion().equals(owningRUMemberVersion)) {
					File bundleFile = new File(memberBundle.getLocation());
					if (bundleFile.isDirectory()) {
						File[] files = bundleFile.listFiles();
						for (File file : files) {
							if (file.isFile() && file.getName().endsWith(".jar")) {
								urlList.add(TCTHelper.toURL(file));
							}
						}
					} else {
						urlList.add(TCTHelper.toURL(bundleFile));
					}
					break;
				}
			}
		}
		return urlList;
	}
	
	public static boolean isHSQL(InstantiatedTPCLShellConfig shellConfig) {
		return getOwningRUID(shellConfig).toString().equals(HSQL_RU_ID);
	}
	
	public static ComponentID getOwningRUID(InstantiatedTPCLShellConfig shellConfig) {
		ComponentID owningRUID = shellConfig.getOwningRUID();
		if (owningRUID == null) {
			owningRUID = new ComponentID(shellConfig.getName() + ".feature");
		}
		return owningRUID;
	}

	public static VersionNumber getOwningRUVersion(InstantiatedTPCLShellConfig shellConfig) {
		VersionNumber owningRUVersion = shellConfig.getOwningRUVersion();
		if (owningRUVersion == null) {
			owningRUVersion = shellConfig.getVersion();
		}
		return owningRUVersion;
	}
	
	public static ComponentID getOwningRUMemberID(InstantiatedTPCLShellConfig shellConfig) {
		ComponentID owningRUMemberID = shellConfig.getOwningRUMemberID();
		if (owningRUMemberID == null) {
			owningRUMemberID = new ComponentID(shellConfig.getName());
		}
		return owningRUMemberID;
	}
	
	public static VersionNumber getOwningRUMemberVersion(InstantiatedTPCLShellConfig shellConfig) {
		VersionNumber owningRUMemberVersion = shellConfig.getOwningRUMemberVersion();
		if (owningRUMemberVersion == null) {
			owningRUMemberVersion = shellConfig.getVersion();
		}
		return owningRUMemberVersion;
	}
}
