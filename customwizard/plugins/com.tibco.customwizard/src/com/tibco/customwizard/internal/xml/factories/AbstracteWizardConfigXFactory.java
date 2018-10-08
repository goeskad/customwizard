package com.tibco.customwizard.internal.xml.factories;

import org.nuxeo.xforms.xml.DefaultXFactory;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.config.ConfigElement;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.support.IWizardConfigAware;
import com.tibco.customwizard.util.PropertyUtils;

public abstract class AbstracteWizardConfigXFactory extends DefaultXFactory {
	public Object create(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
		WizardConfig wizardConfig = (WizardConfig) context.document;
		Object parent = context.peekObject();
		Object object = null;
		try {
			object = create(wizardConfig, localName, parent, attrs);
			if (object != null) {
				if (object instanceof ConfigElement && parent instanceof ConfigElement) {
					((ConfigElement) object).setParent((ConfigElement) parent);
				}
				if (object instanceof IWizardConfigAware) {
					((IWizardConfigAware) object).setWizardConfig(wizardConfig);
				}
				PropertyUtils.setAttributes(object, attrs);
			}
		} catch (Exception e) {
			handleError(wizardConfig, e);
		}
		return object;
	}

	protected abstract Object create(WizardConfig wizardConfig, String name, Object parent, Attributes attrs) throws Exception;

	public void pack(XParserGlobalContext context, Object object) {
		WizardConfig wizardConfig = (WizardConfig) context.document;
		Object parent = context.peekObject();
		try {
			pack(wizardConfig, parent, object);
		} catch (Exception e) {
			handleError(wizardConfig, e);
		}
	}

	protected void pack(WizardConfig wizardConfig, Object parent, Object object) throws Exception {
		// do nothing
	}

	protected Object createObject(WizardConfig wizardConfig, Attributes attrs) throws Exception {
		Object object = wizardConfig.getExtendedClassLoader().loadClass(attrs.getValue("className")).newInstance();
		return object;
	}

	protected void handleError(WizardConfig wizardConfig, Throwable ex) {
		throw new RuntimeException(ex);
	}
}
