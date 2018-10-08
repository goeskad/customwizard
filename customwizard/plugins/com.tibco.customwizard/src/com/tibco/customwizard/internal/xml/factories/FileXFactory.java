package com.tibco.customwizard.internal.xml.factories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.internal.support.URLClassLoaderFactory;
import com.tibco.customwizard.util.WizardHelper;

public class FileXFactory extends AbstracteWizardConfigXFactory {
	public final static FileXFactory INSTANCE = new FileXFactory();

	protected Object create(WizardConfig wizardConfig, String name, Object parent, Attributes attrs) throws Exception {
		String dirStr = attrs.getValue("dir");
		String path = attrs.getValue("path");
		path = path == null ? "" : path;
		
		List<File> fileList = new ArrayList<File>();
		if (dirStr == null) {
			fileList.add(new File(WizardHelper.getAbsolutePath(wizardConfig.getConfigFile(), path)));
		} else {
			File dir = new File(WizardHelper.getAbsolutePath(wizardConfig.getConfigFile(), dirStr));
			String dirPattern = attrs.getValue("dir.pattern");
			List<File> dirList = new ArrayList<File>();
			if (dirPattern == null) {
				dirList.add(dir);
			} else {
				File[] files = dir.listFiles();
				for (File file : files) {
					if (file.isDirectory() && file.getName().matches(dirPattern)) {
						dirList.add(file);
					}
				}
			}
			
			String pattern = attrs.getValue("pattern");
			for (File thisDir : dirList) {
				if (pattern == null) {
					fileList.add(new File(thisDir, path));
				} else {
					File[] files = thisDir.listFiles();
					if (files == null) {
						throw new Exception("There's no file under " + thisDir);
					}
					for (File file : files) {
						checkFileOrDir(file,fileList,pattern);
					}
				}
			}
		}

		for (File file : fileList) {
			((URLClassLoaderFactory) parent).getUrlList().add(WizardHelper.toURL(file));
		}
		return fileList;
	}
	
	/**
	 * If the file is directory, traverse dir and all its subdir, pick out files match the pattern
	 * If the file is file, match it with pattern, if match, add it to fileList.
	 * @param file -- file or directory to check
	 * @param fileList -- List to keep all expected files
	 */
	private void checkFileOrDir(File file, List<File> fileList,String pattern) {
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for (File file2 : files) {
				checkFileOrDir(file2,fileList,pattern);	
			}		
		}
		else if (file.getName().matches(pattern)) {
			fileList.add(file);
		}
	}
}
