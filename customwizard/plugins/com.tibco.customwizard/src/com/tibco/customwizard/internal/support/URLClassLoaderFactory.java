package com.tibco.customwizard.internal.support;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.tibco.customwizard.config.IClassLoaderFactory;
import com.tibco.customwizard.config.WizardConfig;

public class URLClassLoaderFactory implements IClassLoaderFactory {
    private List<URL> urlList = new ArrayList<URL>();

    public List<URL> getUrlList() {
        return urlList;
    }
    
    public ClassLoader createClassLoader(WizardConfig wizardConfig) throws Exception {
        ClassLoader parent = wizardConfig.getExtendedClassLoader();
        if (urlList.isEmpty()) {
            return parent;
        }
        return new URLClassLoader(urlList.toArray(new URL[urlList.size()]), parent);
    }
}
