package com.tibco.customwizard.internal.xml.factories;

import org.xml.sax.Attributes;

import com.tibco.customwizard.action.CustomActionGroup;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.WizardConfig;

public class ActionXFactory extends AbstracteWizardConfigXFactory {
	public final static ActionXFactory INSTANCE = new ActionXFactory();

	protected Object create(WizardConfig wizardConfig, String name, Object parent, Attributes attrs) throws Exception {
		ICustomAction action = (ICustomAction) createObject(wizardConfig, attrs);
		if (parent instanceof CustomActionGroup) {
			((CustomActionGroup) parent).addAction(action);
		}
		return action;
	}
}
