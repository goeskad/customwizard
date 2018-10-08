package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.internal.xforms.actions.SaveModelAction;

public class SaveModelActionXFactory extends ActionXFactory {
    public final static SaveModelActionXFactory INSTANCE = new SaveModelActionXFactory();

    protected Object createInternal(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
        return new SaveModelAction();
    }
}
