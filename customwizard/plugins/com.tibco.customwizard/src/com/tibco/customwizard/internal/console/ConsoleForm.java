package com.tibco.customwizard.internal.console;

import java.util.Iterator;

import org.nuxeo.xforms.ui.AbstractUIControl;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.internal.console.proxies.GroupProxy;
import com.tibco.customwizard.internal.xforms.CWAbstractUIForm;

public class ConsoleForm extends CWAbstractUIForm {
	private GroupProxy rootContainer = new GroupProxy();

	public ConsoleForm(XForm form) {
		super(form);
	}

	public UIControl getProxy(Object nativeControl) {
		return (UIControl) nativeControl;
	}

	public void destroy() {
	}

	public UIControl getParentControl() {
		return rootContainer;
	}

	/**
	 * [Huabin Zhang, 2011/10/25]
	 * To resolve a problem as below:
	 * [Description]
	 * The AbstractUIControl.reset() will not be called unless its isEditable() return true,
	 * and our custom ConsoleControlProxy and its sub-class EditableControlProxy returns false as default,
	 * So the Text-UI of TCT in console mode present incorrectly.
	 * [Solution]
	 * Override AbstractUIForm.reset() method, it will call AbstractUIControl.reset() whatever isEditable()
	 * returns true or false.
	 */
	public void reset() {
		double t0 = System.currentTimeMillis();
		Iterator it = mEditorProxies.values().iterator();
		while (it.hasNext()) {
			AbstractUIControl proxy = (AbstractUIControl)it.next();
			proxy.clear(); // invalidate cached data to force a binding recomputation
//			if (proxy.isEditable()) {
				proxy.reset(); // Call directly proxy.reset().
//			}
		}
		double t1 = System.currentTimeMillis();
		System.out.println(">>> resetting uiform took: "+((t1-t0)/1000));
	}
}
