package com.tibco.customwizard.internal.xml.factories;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Version;
import org.xml.sax.Attributes;

import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.internal.support.URLClassLoaderFactory;
import com.tibco.customwizard.util.WizardHelper;

public class PluginXFactory extends AbstracteWizardConfigXFactory {
	public final static PluginXFactory INSTANCE = new PluginXFactory();

	protected Object create(WizardConfig wizardConfig, String name, Object parent, Attributes attrs) throws Exception {
		String dirStr = attrs.getValue("dir");
		String pluginId = attrs.getValue("pluginId");

		String pattern = pluginId + "_.*";
		List<File> fileList = new ArrayList<File>();
		File dir = new File(WizardHelper.getAbsolutePath(wizardConfig.getConfigFile(), dirStr));
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.getName().matches(pattern)) {
				fileList.add(file);
			}
		}

		fileList = filterFileList(fileList);
		List<File> classpathList = new ArrayList<File>();
		for (File file : fileList) {
			if (file.isDirectory()) {
				File[] internalFiles = file.listFiles();
				for (File internalFile : internalFiles) {
					if (internalFile.getName().endsWith(".jar")) {
						classpathList.add(internalFile);
					}
				}
			} else {
				classpathList.add(file);
			}
		}
		
		for (File file : classpathList) {
			((URLClassLoaderFactory) parent).getUrlList().add(WizardHelper.toURL(file));
		}
		
		return classpathList;
	}

	protected List<File> filterFileList(List<File> fileList) {
		Map<String, String> pluginMap = new HashMap<String, String>();
		Map<String, File> fileMap = new HashMap<String, File>();
		for (File file : fileList) {
			String fullName = getFileName(file);
			int index = fullName.indexOf('_');
			if (index > 0) {
				String pluginId = fullName.substring(0, index);
				String pluginVersion = fullName.substring(index + 1);
				String lastVersion = pluginMap.get(pluginId);
				if (lastVersion == null || compareVersion(lastVersion, pluginVersion) < 0) {
					pluginMap.put(pluginId, pluginVersion);
					fileMap.put(pluginId, file);
				}
			}
		}
		fileList.clear();
		fileList.addAll(fileMap.values());
		return fileList;
	}

	public static String getFileName(File file) {
		String fileName = file.getName();
		if (file.isDirectory()) {
			return fileName;
		} else {
			int index = fileName.lastIndexOf('.');
			return index == -1 ? fileName : fileName.substring(0, index);
		}
	}

	private int compareVersion(String version1, String version2) {
		return new Version(version1).compareTo(new Version(version2));
	}
}
