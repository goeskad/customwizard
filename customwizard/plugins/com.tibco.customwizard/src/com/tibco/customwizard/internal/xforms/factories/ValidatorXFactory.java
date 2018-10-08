package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xforms.model.controls.XFControlElement;
import org.nuxeo.xforms.xml.DefaultXFactory;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.internal.xforms.validator.SimpleControlValidator;
import com.tibco.customwizard.internal.xforms.validator.ValidateManager;
import com.tibco.customwizard.util.PropertyUtils;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.customwizard.validator.ICustomValidator;

public class ValidatorXFactory extends DefaultXFactory {
    public final static ValidatorXFactory INSTANCE = new ValidatorXFactory();

    public Object create(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
        String className = attrs.getValue("className");
        try {
        	ICustomValidator validator;
            if (className == null) {
                validator = new SimpleControlValidator(attrs.getValue("validation"));
            } else {
                validator = (ICustomValidator) XFormUtils.getContextClassLoader(context).loadClass(className).newInstance();
                PropertyUtils.setAttributes(validator, attrs);
            }
            
            Object parent = context.peekObject();
            if (parent instanceof XFControlElement) {
                ValidateManager.addValidator((XFControlElement) parent, validator);
            } else {
                System.out.println("Warning: " + localName + " defined outside a control element.");
            }
            return validator;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
