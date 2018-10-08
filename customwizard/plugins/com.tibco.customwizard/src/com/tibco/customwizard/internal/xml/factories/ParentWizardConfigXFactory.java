package com.tibco.customwizard.internal.xml.factories;

import org.xml.sax.Attributes;

import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.util.WizardHelper;

public class ParentWizardConfigXFactory extends AbstracteWizardConfigXFactory {
	public final static ParentWizardConfigXFactory INSTANCE = new ParentWizardConfigXFactory();

	protected Object create(WizardConfig wizardConfig, String name, Object parent, Attributes attrs) throws Exception {
		String wizardId = attrs.getValue("id");
		WizardConfig parentWizardConfig = WizardHelper.getWizardConfigLocator().findWizardConfig(wizardId);
		if (parentWizardConfig != null) {
			wizardConfig.setParentWizardConfig(parentWizardConfig);
			wizardConfig.setExtendedClassLoader(parentWizardConfig.getExtendedClassLoader());
		}else
		{
			System.out.println("!!!!!!!!!!!!");
		}
		return parentWizardConfig;
	}
}
