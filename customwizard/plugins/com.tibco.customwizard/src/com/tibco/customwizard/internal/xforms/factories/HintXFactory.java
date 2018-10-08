package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xforms.model.controls.XFControlElement;
import org.nuxeo.xforms.xml.XParserGlobalContext;

/**
 * The method "setHint" in XFControlElement is wrong, so we need to use AttributesMap to set hint
 *
 */
public class HintXFactory extends org.nuxeo.xforms.xforms.factories.HintXFactory {

    public final static HintXFactory INSTANCE = new HintXFactory();

    @Override
    public void setContent(XParserGlobalContext context, Object object, String text) {
        Object parent = context.peekObject();
        if (parent instanceof XFControlElement) {
            ((XFControlElement) parent).setHint(text);
            if (((XFControlElement) parent).getHint() == null) {
                ((XFControlElement) parent).getAttributes().put("hint", text);
            }
        } else {
            System.out.println("Parse error: hint element out of it's scope"); // TODO
        }
    }

}
