package com.tibco.customwizard.internal.support;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.tibco.customwizard.config.IURLProvider;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.support.IWizardConfigAware;
import com.tibco.customwizard.util.WizardHelper;

public class LibURLProvider implements IURLProvider, IWizardConfigAware {
    private WizardConfig wizardConfig;

    private String lib = "lib";

    public void setLib(String lib) {
        this.lib = lib;
    }

    public void setWizardConfig(WizardConfig wizardConfig) {
        this.wizardConfig = wizardConfig;
    }

    public void addUrls(List<URL> urlList) throws Exception {
        setUrls(urlList, new File(WizardHelper.getAbsolutePath(wizardConfig.getConfigFile(), lib)), new ExtNameFileFilter(".jar"), false);
    }

    protected void setUrls(List<URL> urlList, File dir, FileFilter fileFilter, boolean deep) throws MalformedURLException {
        File[] files = dir.listFiles(fileFilter);
        if (files != null) {
            for (File file : files) {
                urlList.add(WizardHelper.toURL(file));
                if (deep && file.isDirectory()) {
                    setUrls(urlList, file, fileFilter, deep);
                }
            }
        }
    }

    protected static class ExtNameFileFilter implements FileFilter {
        private String extName;

        public ExtNameFileFilter(String extName) {
            this.extName = extName;
        }

        public boolean accept(File pathname) {
            return pathname.getName().endsWith(extName);
        }
    }
}
