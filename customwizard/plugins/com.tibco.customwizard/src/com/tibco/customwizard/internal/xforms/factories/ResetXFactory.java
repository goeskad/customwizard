package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.internal.xforms.actions.ResetAction;

public class ResetXFactory extends ActionXFactory {
	public final static ResetXFactory INSTANCE = new ResetXFactory();

	protected Object createInternal(XParserGlobalContext context, String uri, String localName, String qname,
			Attributes attrs) {
		return new ResetAction();
	}
}
