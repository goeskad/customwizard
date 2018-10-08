package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xforms.model.actions.XFActionGroup;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

public class ActionGroupXFactory extends ActionXFactory {
	public final static ActionGroupXFactory INSTANCE = new ActionGroupXFactory();

	protected Object createInternal(XParserGlobalContext context, String uri, String localName, String qname,
			Attributes attrs) {
		return new XFActionGroup();
	}
}
