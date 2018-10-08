package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xforms.model.actions.XFAction;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.internal.xforms.actions.OpenURLAction;

public class OpenURLActionXFactory extends ActionXFactory {
	public final static OpenURLActionXFactory INSTANCE = new OpenURLActionXFactory();

    protected XFAction createInternal(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
        return new OpenURLAction();
    }
}
