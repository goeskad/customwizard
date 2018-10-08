package com.tibco.customwizard.internal.xml.factories;

import org.xml.sax.Attributes;

import com.tibco.customwizard.config.PageConfig;
import com.tibco.customwizard.config.PageGroupConfig;
import com.tibco.customwizard.config.WizardConfig;

public class PageXFactory extends AbstracteWizardConfigXFactory {
	public final static PageXFactory INSTANCE = new PageXFactory();

	protected Object create(WizardConfig wizardConfig, String name,
			Object parent, Attributes attrs) throws Exception {
		PageConfig pageConfig = new PageConfig();
		PageGroupConfig pageGroup = (PageGroupConfig) parent;
		pageGroup.getPageList().add(pageConfig);
		return pageConfig;
	}
}
