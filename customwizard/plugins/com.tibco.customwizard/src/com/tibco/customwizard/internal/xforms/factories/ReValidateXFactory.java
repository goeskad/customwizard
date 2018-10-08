package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.internal.xforms.actions.ReValidateAction;

public class ReValidateXFactory extends ActionXFactory {
	public final static ReValidateXFactory INSTANCE = new ReValidateXFactory();

	protected Object createInternal(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
		return new ReValidateAction();
	}
}
