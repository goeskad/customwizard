package com.tibco.customwizard.config;





public interface IClassLoaderFactory {
    public ClassLoader createClassLoader(WizardConfig wizardConfig) throws Exception;
}
