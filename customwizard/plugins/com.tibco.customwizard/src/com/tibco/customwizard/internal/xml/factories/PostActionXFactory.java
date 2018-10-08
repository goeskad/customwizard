package com.tibco.customwizard.internal.xml.factories;

import org.xml.sax.Attributes;

import com.tibco.customwizard.action.CustomActionGroup;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.WizardConfig;

public class PostActionXFactory extends AbstracteWizardConfigXFactory {
	public final static PostActionXFactory INSTANCE = new PostActionXFactory();

	protected Object create(WizardConfig wizardConfig, String name, Object parent, Attributes attrs) throws Exception {
		ICustomAction action = new CustomActionGroup();
		wizardConfig.setPostAction(action);
		return action;
	}
}
