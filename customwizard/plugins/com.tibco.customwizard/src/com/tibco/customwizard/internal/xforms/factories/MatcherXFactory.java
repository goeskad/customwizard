package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xforms.model.controls.XFControlElement;
import org.nuxeo.xforms.xml.DefaultXFactory;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.internal.xforms.validator.MatchValueValidator;
import com.tibco.customwizard.internal.xforms.validator.ValidateManager;

public class MatcherXFactory extends DefaultXFactory {
    public final static MatcherXFactory INSTANCE = new MatcherXFactory();

    public Object create(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
        MatchValueValidator validator = new MatchValueValidator();
        validator.setErrorMessage(attrs.getValue("errorMessage"));

        Object parent = context.peekObject();
        if (parent instanceof XFControlElement) {
            ValidateManager.addValidator((XFControlElement) parent, validator);
        } else {
            System.out.println("Warning: " + localName + " defined outside a control element.");
        }
        return validator;
    }
}
