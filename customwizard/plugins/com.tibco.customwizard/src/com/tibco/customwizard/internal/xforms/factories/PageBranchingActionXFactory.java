package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xforms.model.actions.XFAction;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.internal.xforms.actions.PageBranchingAction;

public class PageBranchingActionXFactory extends ActionXFactory {
    public final static PageBranchingActionXFactory INSTANCE = new PageBranchingActionXFactory();

    protected XFAction createInternal(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
        PageBranchingAction action = new PageBranchingAction();
        action.setRef(attrs.getValue("ref"));
        return action;
    }
}
