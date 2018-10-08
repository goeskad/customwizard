package com.tibco.tct.admin;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.tct.framework.actions.OptionAction;

public class InternalHttpSSLAction extends OptionAction {
	protected void selectionAction(boolean selected, XForm form, IActionContext actionContext) {
		UIControl urlControl = form.getUI().getControl("baseurl");
		// [Huabin Zhang, 2011.08.17]
		// To fix ticket TOOL-998:
		// I removed view element from xform, but baseurl control is used by
		// this action, so I have to valide if it's null.
		// BTW: In the future, maybe we'll use CSS style to set view element to
		// be invisible simply.
		if (urlControl != null) {
			String url = urlControl.getXMLValue();
			if (selected && url.indexOf("http://") == 0) {
				url = url.replace("http://", "https://");
				urlControl.setXMLValue(url);
	
			} else if (!selected && url.indexOf("https://") == 0) {
				url = url.replace("https://", "http://");
				urlControl.setXMLValue(url);
			}
		}
	}
}
