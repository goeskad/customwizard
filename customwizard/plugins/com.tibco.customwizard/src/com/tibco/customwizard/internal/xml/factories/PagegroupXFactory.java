package com.tibco.customwizard.internal.xml.factories;

import org.xml.sax.Attributes;

import com.tibco.customwizard.config.PageGroupConfig;
import com.tibco.customwizard.config.WizardConfig;

public class PagegroupXFactory extends AbstracteWizardConfigXFactory {
	public final static PagegroupXFactory INSTANCE = new PagegroupXFactory();

	protected Object create(WizardConfig wizardConfig, String name,
			Object parent, Attributes attrs) throws Exception {
		PageGroupConfig pageGroup = new PageGroupConfig();
		wizardConfig.getPageGroupList().add(pageGroup);
		return pageGroup;
	}
}
