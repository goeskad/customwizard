package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xml.DefaultXFactory;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.action.CustomActionGroup;
import com.tibco.customwizard.util.XFormUtils;

public class PageActionXFactory extends DefaultXFactory {
	public final static PageActionXFactory INSTANCE = new PageActionXFactory();

	private static final String PRE_ACTION = "preaction";
	private static final String POST_ACTION = "postaction";
	
	public Object create(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
		CustomActionGroup action = new CustomActionGroup();

		if (localName.equals(PRE_ACTION)) {
			XForm xform = (XForm) context.document;
			XFormUtils.getPageInstance(xform).setPreAction(action);
		} else if (localName.equals(POST_ACTION)) {
			XForm xform = (XForm) context.document;
			XFormUtils.getPageInstance(xform).setPostAction(action);
		}
		return action;
	}
}
