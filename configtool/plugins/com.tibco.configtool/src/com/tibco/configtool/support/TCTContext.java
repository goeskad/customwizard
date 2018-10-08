package com.tibco.configtool.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

import com.tibco.configtool.internal.support.DefaultHostnameVerifier;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.WizardApplicationContext;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.neo.model.types.VersionNumber;

public class TCTContext {
	public static final String HOST_NAME_PROPERTY = "tct.hostname";
	public static final String TCT_HOME_PROPERTY = "tct.home";
	
	private static TCTContext instance;

	private WizardInstance wizardInstance;

	private boolean consoleMode;
	
	private boolean silentMode;
	private String silentContributorId;
	private String silentDataFile;
	private String silentTargetName;
	
	private String sessionFile;
	private String tibcoHome;
	private String tibcoConfigHome;
	private String machineModelLocation;
	private String amxVersion;
	
	private boolean configHomeUpdated = false;
	
	private String keystorePath;
	
	private File contributorDir;
	private File tctHome;
	private File workDir;
	
	private URL tctJarUrl;

	private boolean processingLoadAction = false;
	
	private Map<String, WizardConfig> contributorMap;
	
	private Map<WizardConfig, WizardInstance> instanceMap = new HashMap<WizardConfig, WizardInstance>();
	
	public static void initInstance(WizardConfig wizardConfig) throws Exception {
		instance = new TCTContext(wizardConfig);
	}

	public static TCTContext getInstance() {
		return instance;
	}

	private TCTContext(WizardConfig wizardConfig) throws Exception {
		wizardInstance = new WizardInstance(wizardConfig);
		
		tibcoHome = TCTHelper.toUnixPath(System.getProperty("tibco.home"));
		System.setProperty("tibco.home", tibcoHome);
		tibcoConfigHome = TCTHelper.toUnixPath(System.getProperty("tct.config.home"));
		System.setProperty("tct.config.home", tibcoConfigHome);
		machineModelLocation = TCTHelper.toUnixPath(System.getProperty("tct.machine.model.location"));
		System.setProperty("tct.machine.model.location", machineModelLocation);
		amxVersion = System.getProperty("amx.version");
		if (amxVersion == null) {
			amxVersion = "3.2";
			System.setProperty("amx.version", amxVersion);
		}
		
		configHomeUpdated = Boolean.parseBoolean(System.getProperty("tct.config.home.updated"));
		
		tctHome = new File(wizardConfig.getConfigFile().getFile()).getParentFile().getParentFile().getParentFile()
				.getParentFile();
		
		tctJarUrl = new URL(wizardConfig.getConfigFile(), "tct.jar");

		String contributorDirStr = System.getProperty("tct.contributor.dir");
		if (contributorDirStr == null) {
			contributorDir = new File(wizardConfig.getConfigFile().getFile()).getParentFile().getParentFile();
		} else {
			contributorDir = new File(contributorDirStr);
		}
		
		try {
			System.setProperty(TCT_HOME_PROPERTY, tctHome.getAbsolutePath());
			System.setProperty(HOST_NAME_PROPERTY, InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			System.setProperty(HOST_NAME_PROPERTY, "localhost");
		}

		// process application args
		consoleMode = WizardApplicationContext.hasArg("-consoleMode");
		silentMode = WizardApplicationContext.hasArg("-silentMode");
		silentContributorId = WizardApplicationContext.getArgValue("-contributor.id");
		silentDataFile = WizardApplicationContext.getArgValue("-contributor.props");
		silentTargetName = WizardApplicationContext.getArgValue("-contributor.target");
		
		sessionFile = WizardApplicationContext.getArgValue("-tct.install.session.file");
		
		// Since trinity API may initializing the https setting with old java API
		// so we need to use this to initializing the https URL stream handler in jvm with default setting
		// the value "https://localhost:8120" doesn't mean anything, it can be any other url
		new URL("https://localhost:8120");
		// The default host name verifier always return true since we don't need to verifier for host name
		// This verifier has to be set otherwise the local machine name could not be verified
		HttpsURLConnection.setDefaultHostnameVerifier(new DefaultHostnameVerifier());
	}

	public boolean isConfigHomeUpdated() {
		return configHomeUpdated;
	}
	
	public boolean isConsoleMode() {
		return consoleMode;
	}

	public boolean isSilentMode() {
		return silentMode;
	}

	public String getSilentContributorId() {
		return silentContributorId;
	}

	public String getSilentDataFile() {
		return silentDataFile;
	}

	public String getSilentTargetName() {
		return silentTargetName;
	}

	public String getSessionFile() {
		return sessionFile;
	}

	public WizardInstance getWizardInstance() {
		return wizardInstance;
	}

	public void setWizardInstance(WizardInstance wizardInstance) {
		this.wizardInstance = wizardInstance;
	}

	public WizardInstance getWizardInstance(WizardConfig wizardConfig) {
		return instanceMap.get(wizardConfig);
	}

	public void setWizardInstance(WizardConfig wizardConfig, WizardInstance wizardInstance) {
		instanceMap.put(wizardConfig, wizardInstance);
	}
	
	public String getTibcoHome() {
		return tibcoHome;
	}

	public String getTibcoConfigHome() {
		return tibcoConfigHome;
	}

	public void setTibcoConfigHome(String tibcoConfigHome) {
		tibcoConfigHome = TCTHelper.toUnixPath(tibcoConfigHome);
		
		//update TIBCOConfigurationTool.ini
		try {
			File iniFile = new File(getTCTHome(), "TIBCOConfigurationTool.ini");
			String content = new String(TCTHelper.readFile(iniFile));
			String updatedFlag = "";
			if (!configHomeUpdated) {
				configHomeUpdated = true;
				updatedFlag = "\r\n-Dtct.config.home.updated=true";
			}
			content = content.replace("-Dtct.config.home=" + System.getProperty("tct.config.home"), "-Dtct.config.home=" + tibcoConfigHome + updatedFlag);
			TCTHelper.writeFile(iniFile, content.getBytes());
		} catch (IOException e) {
		}
		
		this.tibcoConfigHome = tibcoConfigHome;
		System.setProperty("tct.config.home", tibcoConfigHome);
		workDir = new File(tibcoConfigHome, "tct");
		
		File keystoreDir = new File(workDir, "keystore");
		keystoreDir.mkdirs();
		
		keystorePath = TCTHelper.toUnixPath(keystoreDir.getAbsolutePath());
	}

	public String getMachineModelLocation() {
		return machineModelLocation;
	}

	public String getAmxVersion() {
		return amxVersion;
	}

	public File getTCTHome() {
		return tctHome;
	}

	public File getWorkDir() {
		return workDir;
	}

	public String getKeystorePath() {
		return keystorePath;
	}

	public URL getTctJarUrl() {
		return tctJarUrl;
	}

	public File getContributorDir() {
		return contributorDir;
	}

	public boolean isProcessingLoadAction() {
		return processingLoadAction;
	}

	public void setProcessingLoadAction(boolean processingLoadAction) {
		this.processingLoadAction = processingLoadAction;
	}
	
	public List<WizardConfig> getContributorList() {
		contributorMap = getContributorMap();
		List<WizardConfig> contributorList = new ArrayList<WizardConfig>();
		TreeMap<Integer, WizardConfig> sortMap = new TreeMap<Integer, WizardConfig>();
		for (WizardConfig wizardConfig : contributorMap.values()) {
			String sequence = wizardConfig.getSequence();
			String configFile = new File(wizardConfig.getConfigFile().getFile()).getAbsolutePath();
			if (sequence == null) {
				throw new ActionException("Please specify \"sequence\" in " + configFile);
			}
			
			WizardConfig conflict = sortMap.get(new Integer(sequence));
			if (conflict != null) {
				throw new ActionException("\"sequence\" conflict between " + configFile + " and "
						+ new File(conflict.getConfigFile().getFile()).getAbsolutePath());
			}
			
			sortMap.put(new Integer(sequence), wizardConfig);
		}
		contributorList.addAll(sortMap.values());
		return contributorList;
	}

	public WizardConfig getContributor(String id) {
		return getContributorMap().get(id);
	}
	
	public Map<String, WizardConfig> getContributorMap() {
		if (contributorMap == null) {
			FileInputStream sessionInput = null;

			WizardConfig wizardConfig = wizardInstance.getWizardConfig();
			try {
				Properties contribProps = getContribProps();

				Properties sessionProps = new Properties();
				String sessionFile = TCTContext.getInstance().getSessionFile();
				if (sessionFile != null && sessionFile.length() > 0) {
					sessionInput = new FileInputStream(WizardHelper.getAbsolutePath(wizardConfig.getConfigFile(), sessionFile));
					sessionProps.load(sessionInput);
				} else {
					sessionProps.putAll(contribProps);
				}
				filterLatestVersion(sessionProps);
				contributorMap = new HashMap<String, WizardConfig>();
				for (Object key : sessionProps.keySet()) {
					String configLocation = contribProps.getProperty(key.toString());
					if (configLocation != null) {
						WizardConfig contribConfig = WizardHelper.loadWizardConfig(TCTHelper.toURL(new File(configLocation)), wizardConfig
								.getExtendedClassLoader());
						contributorMap.put(getContributorId(key.toString()), contribConfig);
					}
				}
			} catch (Exception e) {
				throw new ActionException(e);
			} finally {
				if (sessionInput != null) {
					try {
						sessionInput.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return contributorMap;
	}

	public String getContributorId(WizardInstance wizardInstance) {
		URL configFile = wizardInstance.getWizardConfig().getConfigFile();
		String pluginName = new File(configFile.getFile()).getParentFile().getName();
		return getContributorId(pluginName);
	}
	
	public String getContributorId(String pluginName) {
		int index = pluginName.indexOf('_');
		if (index > 0) {
			return pluginName.substring(0, index);
		}
		return pluginName;
	}

	private Properties getContribProps() throws Exception {
		Properties contribProps = new Properties();
		File pluginsDir = getContributorDir();
		File[] plugins = pluginsDir.listFiles();
		for (File plugin : plugins) {
			File wizardConfigFile = new File(plugin, "WizardConfig.xml");
			if (plugin.isDirectory() && wizardConfigFile.exists() && !plugin.getName().startsWith("com.tibco.configtool_")) {
				contribProps.setProperty(plugin.getName(), wizardConfigFile.getAbsolutePath());
			}
		}
		return contribProps;
	}

	private void filterLatestVersion(Properties sessionProps) {
		Properties tempProps = (Properties) sessionProps.clone();
		Map<String, String> contributorMap = new HashMap<String, String>();
		for (Object key : tempProps.keySet()) {
			String contributor = (String) key;
			int index = contributor.indexOf('_');
			if (index > 0) {
				String contributorId = contributor.substring(0, index);
				String contributorVersion = contributor.substring(index + 1);
				String lastVersion = contributorMap.get(contributorId);
				if (lastVersion == null || compareVersion(lastVersion, contributorVersion) < 0) {
					contributorMap.put(contributorId, contributorVersion);
				}

				if (lastVersion != null) {
					if (compareVersion(lastVersion, contributorVersion) < 0) {
						sessionProps.remove(contributorId + "_" + lastVersion);
					} else {
						sessionProps.remove(contributorId + "_" + contributorVersion);
					}
				}
			}
		}
	}

	private int compareVersion(String version1, String version2) {
		return new VersionNumber(version1).compareTo(new VersionNumber(version2));
	}
}
