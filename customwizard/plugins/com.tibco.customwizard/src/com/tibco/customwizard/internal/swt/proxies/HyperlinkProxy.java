package com.tibco.customwizard.internal.swt.proxies;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nuxeo.xforms.ui.swt.BuildContext;
import org.nuxeo.xforms.ui.swt.proxies.SWTControlProxy;

public class HyperlinkProxy extends SWTControlProxy {
	public Control create(BuildContext context, Composite parent, int style) {
		Hyperlink hl = new Hyperlink(parent, style);
		hl.setText("");
		return hl;
	}

	public void setXMLValue(String value) {
		((Hyperlink) control).setText(value == null ? "" : value);
	}

	public String getXMLValue() {
		return ((Hyperlink) control).getText();
	}
}
