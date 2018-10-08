package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xml.DefaultXFactory;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

public class XFormXFactory extends DefaultXFactory {
	public final static XFormXFactory INSTANCE = new XFormXFactory();

	public Object create(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
		// do nothing
		return null;
	}
}
