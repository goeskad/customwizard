package com.tibco.customwizard.internal.support;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.tibco.customwizard.config.IClassLoaderFactory;
import com.tibco.customwizard.config.WizardConfig;

public class BundleClassLoaderFactory implements IClassLoaderFactory {
    public ClassLoader createClassLoader(WizardConfig wizardConfig) throws Exception {
        File bundleDir = new File(wizardConfig.getConfigFile().getFile()).getParentFile();
        String bundleName = bundleDir.getName();
        String pattern = "(.*)_(\\d+\\.\\d+\\.\\d+.*)";
        Matcher matcher = Pattern.compile(pattern).matcher(bundleName);
        matcher.matches();
        System.out.println(matcher.group(1));
        Bundle bundle = Platform.getBundle(matcher.group(1));
        String testClass = searchClass(bundleDir, bundleDir);

        return bundle.loadClass(testClass).getClassLoader();
    }

    private String searchClass(File root, File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String className = searchClass(root, file);
                    if (className != null) {
                        return className;
                    }
                } else if (file.getName().endsWith(".class")) {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    while (!(file = file.getParentFile()).equals(root)) {
                        className = file.getName() + "." + className;
                    }
                    return className;
                }
            }
        }
        return null;
    }
}
