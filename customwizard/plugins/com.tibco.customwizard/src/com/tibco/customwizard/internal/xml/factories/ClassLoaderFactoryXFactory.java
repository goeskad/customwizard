package com.tibco.customwizard.internal.xml.factories;

import org.xml.sax.Attributes;

import com.tibco.customwizard.config.IClassLoaderFactory;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.internal.support.URLClassLoaderFactory;

public class ClassLoaderFactoryXFactory extends AbstracteWizardConfigXFactory {
	public final static ClassLoaderFactoryXFactory INSTANCE = new ClassLoaderFactoryXFactory();

	protected Object create(WizardConfig wizardConfig, String name, Object parent, Attributes attrs) throws Exception {
		IClassLoaderFactory classLoaderFactory = (IClassLoaderFactory) createObject(wizardConfig, attrs);
		return classLoaderFactory;
	}

	protected void pack(WizardConfig wizardConfig, Object parent, Object object) throws Exception {
		wizardConfig.setExtendedClassLoader(((URLClassLoaderFactory) object).createClassLoader(wizardConfig));
	}
}
