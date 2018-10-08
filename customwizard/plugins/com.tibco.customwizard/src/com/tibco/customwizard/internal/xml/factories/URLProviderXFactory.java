package com.tibco.customwizard.internal.xml.factories;

import org.xml.sax.Attributes;

import com.tibco.customwizard.config.IURLProvider;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.internal.support.URLClassLoaderFactory;

public class URLProviderXFactory extends AbstracteWizardConfigXFactory {
	public final static URLProviderXFactory INSTANCE = new URLProviderXFactory();

	protected Object create(WizardConfig wizardConfig, String name, Object parent, Attributes attrs) throws Exception {
		IURLProvider urlProvider = (IURLProvider) createObject(wizardConfig, attrs);
		return urlProvider;
	}
	
	protected void pack(WizardConfig wizardConfig, Object parent, Object object) throws Exception {
		if (parent instanceof URLClassLoaderFactory) {
			((IURLProvider) object).addUrls(((URLClassLoaderFactory) parent).getUrlList());
		}
	}
}
