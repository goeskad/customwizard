package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.internal.xforms.actions.RefreshAction;

public class RefreshXFactory extends ActionXFactory {
	public final static RefreshXFactory INSTANCE = new RefreshXFactory();

	protected Object createInternal(XParserGlobalContext context, String uri, String localName, String qname,
			Attributes attrs) {
		return new RefreshAction();
	}
}
