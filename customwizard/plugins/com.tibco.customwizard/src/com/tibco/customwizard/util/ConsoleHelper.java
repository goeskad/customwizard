package com.tibco.customwizard.util;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.UIForm;

import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.internal.console.ConsoleForm;
import com.tibco.customwizard.internal.console.ConsoleXFormExtension;
import com.tibco.customwizard.internal.console.proxies.CheckBoxProxy;
import com.tibco.customwizard.internal.console.proxies.ComboProxy;
import com.tibco.customwizard.internal.console.proxies.ConsoleControlProxy;
import com.tibco.customwizard.internal.console.proxies.GroupProxy;

public class ConsoleHelper {
	private static ConsoleXFormExtension xformExtension = new ConsoleXFormExtension();
	
	public static UIForm buildUI(PageInstance pageInstance) {
		UIForm uiForm = xformExtension.getUIBuilder().build(XFormUtils.getXForm(pageInstance), null);
		WizardHelper.validate(pageInstance);
		return uiForm;
	}

	public static UIControl createControl(UIControl parent, Class<?> controlClass) throws Exception {
		ConsoleControlProxy control = (ConsoleControlProxy) controlClass.newInstance();
		ConsoleForm uiForm = (ConsoleForm) parent.getElement().getForm().getUI();
		control.create(uiForm, null);
		uiForm.registerControlProxy(control);
		if (parent instanceof GroupProxy) {
			((GroupProxy) parent).addChildControl(control);
		}
		return control;
	}
	
	public static UIControl createCheckBox(UIControl parent) throws Exception {
		return createControl(parent, CheckBoxProxy.class);
	}

	public static void updateCombo(UIControl comboProxy, String[] values, String[] labels) {
		ComboProxy combo = (ComboProxy) comboProxy;
		combo.removeAllItems();
		for (int i = 0; i < values.length; i++) {
			combo.addItem(values[i], labels[i]);
		}
	}
}
