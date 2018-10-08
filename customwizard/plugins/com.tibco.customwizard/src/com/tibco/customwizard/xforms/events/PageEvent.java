package com.tibco.customwizard.xforms.events;

import org.nuxeo.xforms.xforms.events.XFormsEvent;

public class PageEvent extends XFormsEvent {
    public PageEvent(String id, Object target, Object nativeEvent) {
        super(id, target, nativeEvent);
    }
}
