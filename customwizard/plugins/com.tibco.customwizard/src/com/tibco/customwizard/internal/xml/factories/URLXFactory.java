package com.tibco.customwizard.internal.xml.factories;

import java.net.URL;

import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.internal.support.FinalObjectWrapper;
import com.tibco.customwizard.internal.support.URLClassLoaderFactory;

public class URLXFactory extends AbstracteWizardConfigXFactory {
	public final static URLXFactory INSTANCE = new URLXFactory();

	protected Object create(WizardConfig wizardConfig, String name, Object parent, Attributes attrs) throws Exception {
		return new FinalObjectWrapper();
	}

	public void setContent(XParserGlobalContext context, Object object, String text) {
		((FinalObjectWrapper) object).setObject(text);
	}

	public boolean isContentNode(XParserGlobalContext context, Object object) {
		return true;
	}

	protected void pack(WizardConfig wizardConfig, Object parent, Object object) throws Exception {
		if (parent instanceof URLClassLoaderFactory) {
			URL url = new URL((String) ((FinalObjectWrapper) object).getObject());
			((URLClassLoaderFactory) parent).getUrlList().add(url);
		}
	}
}
