package com.tibco.ert.model.utils;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AMXClassLoaderFactory {
	public static ClassLoader createClassLoader(String baseDir, String strCLILocation, ClassLoader parent)
			throws Exception {
		return createClassLoader(baseDir, getAMXPluginsDir(strCLILocation), parent);
	}
	
	@SuppressWarnings("deprecation")
	public static ClassLoader createClassLoader(String baseDir, File amxPlugins, ClassLoader parent) throws Exception {
		List<URL> urlList = new ArrayList<URL>();

		urlList.add(new File(baseDir, "ert.jar").toURL());
		
		boolean valid = false;

		File[] files = amxPlugins.listFiles();
		Map<String, File> fileMap = new HashMap<String, File>();
		for (File file : files) {
			if (file.isDirectory()) {
				String pluginName = file.getName();
				int index = pluginName.indexOf('_');
				if (index > 0) {
					pluginName = pluginName.substring(0, index);
				}

				if (pluginName.endsWith(".lib")) {
					continue;
				}

				File lastFile = fileMap.get(pluginName);
				if (lastFile == null || file.lastModified() > lastFile.lastModified()) {
					fileMap.put(pluginName, file);
				}
			} else if (file.getName().startsWith("com.tibco.security.tibcrypt_")
					|| file.getName().startsWith("org.eclipse.equinox.common_")
					|| file.getName().startsWith("com.tibco.neo.common.util_")
					|| file.getName().startsWith("com.tibco.matrix.administration.server_")) {
				urlList.add(file.toURL());
			}
		}
		files = fileMap.values().toArray(new File[fileMap.size()]);

		for (File file : files) {
			if (file.getName().startsWith("com.tibco.matrix.administration.command.line_")) {
				valid = true;
				urlList.add(file.toURL());
			} else if (file.getName().startsWith("com.tibco.tpcl.org.codehaus.xfire.core_")) {
				urlList.add(new File(file, "xfire-tibco-patch.jar").toURL());
			}

			setUrls(urlList, file, new ExtNameFileFilter(".jar"), false);
			if (file.getName().startsWith("org.apache.ant_")) {
				File antJar = new File(file, "lib/ant.jar");
				if (antJar.exists()) {
					urlList.add(antJar.toURL());
				}
			}
		}

		if (!valid || urlList.size() < 20) {
			throw new Exception("Please specify the amx plugins directory correctly");
		}
		
		return new URLClassLoader(urlList.toArray(new URL[urlList.size()]), parent);
	}

	private static File getAMXPluginsDir(String strCLILocation) throws Exception {
		if (strCLILocation == null || strCLILocation.length() == 0) {
			throw new Exception(
					"Please use the \"Configuration\" wizard to specify the file path of the admincmdline.exe first");
		}

		File amxPlugins = null;
		try {
			File cliLocation = new File(strCLILocation);
			amxPlugins = new File(cliLocation.getParentFile().getParentFile().getParentFile().getParentFile(),
					"components/eclipse/plugins");
		} catch (Exception e) {
			throw new Exception("Please specify the file path of the admincmdline.exe correctly");
		}

		if (!amxPlugins.exists() || !amxPlugins.isDirectory()) {
			throw new Exception("Please specify the file path of the admincmdline.exe correctly");
		}

		return amxPlugins;
	}
	
	@SuppressWarnings("deprecation")
	private static void setUrls(List<URL> urlList, File dir, FileFilter fileFilter, boolean deep)
			throws MalformedURLException {
		File[] files = dir.listFiles(fileFilter);
		for (File file : files) {
			urlList.add(file.toURL());
			if (deep && file.isDirectory()) {
				setUrls(urlList, file, fileFilter, deep);
			}
		}
	}

	private static class ExtNameFileFilter implements FileFilter {
		private String extName;

		public ExtNameFileFilter(String extName) {
			this.extName = extName;
		}

		public boolean accept(File pathname) {
			return pathname.getName().endsWith(extName);
		}
	}
}
