package com.tibco.customwizard.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.UIForm;

import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.internal.console.proxies.ConsoleControlProxy;
import com.tibco.customwizard.internal.console.proxies.GroupProxy;
import com.tibco.customwizard.internal.swt.proxies.FileChooserProxy;

public class ControlUtils {
	public static List<UIControl> getChildren(UIControl container) {
		List<UIControl> controlList = new ArrayList<UIControl>();
		if (container instanceof GroupProxy) {
			List<ConsoleControlProxy> consoleControlList = ((GroupProxy) container).getControlList();
			controlList.addAll(consoleControlList);
		} else if (container.getControl() instanceof Composite) {
			UIForm uiForm = container.getElement().getForm().getUI();
			Composite composite = (Composite) container.getControl();
			Control[] children = composite.getChildren();
			for (Control control : children) {
				UIControl proxy = uiForm.getProxy(control);
				if (proxy != null) {
					controlList.add(proxy);
				}
			}
		}
		return controlList;
	}

	public static UIControl createControl(UIControl parent, String className) throws Exception {
		return createControl(parent, className, 0);
	}

	public static UIControl createControl(UIControl parent, String className, int style) throws Exception {
		WizardInstance wizardInstance = XFormUtils.getWizardInstance(parent);
		Class<?> controlClass = wizardInstance.getWizardConfig().getExtendedClassLoader().loadClass(className);
		if (WizardHelper.isSWTMode(wizardInstance)) {
			return SWTHelper.createControl(parent, controlClass, style);
		} else {
			return ConsoleHelper.createControl(parent, controlClass);
		}
	}

	public static void setVisible(UIControl control, boolean visible) {
		if (control instanceof ConsoleControlProxy) {
			control.setEnabled(visible);
		} else if (control.getControl() instanceof Control) {
			((Control) control.getControl()).setVisible(visible);
		}
	}

	public static boolean isEnabled(UIControl control) {
		if (control instanceof ConsoleControlProxy) {
			return ((ConsoleControlProxy) control).isEnabled();
		} else if (control.getControl() instanceof Control) {
			return ((Control) control.getControl()).isEnabled();
		}
		return false;
	}
	
	/**
	 * Set the file filter for filechooser
	 * @param fileChooser
	 * @param fileType
	 */
	public static void setFilterExtensions(UIControl fileChooser, String fileType){
		FileChooserProxy fileChooserProxy = (FileChooserProxy)fileChooser;
		fileChooserProxy.setFilterExtensions(fileType);
	}
}
