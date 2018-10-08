package com.tibco.customwizard.xforms.events;


public class PageShownEvent extends PageEvent {
    public final static String ID = "pageshown";
    
    public PageShownEvent() {
        super(ID, null, null);
    }
}
