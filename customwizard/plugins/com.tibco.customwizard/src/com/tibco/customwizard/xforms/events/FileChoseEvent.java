package com.tibco.customwizard.xforms.events;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.events.UIControlEvent;

public class FileChoseEvent extends UIControlEvent {

    public final static String ID = "FileChose";

    public FileChoseEvent(UIControl target, Object nativeEvent) {
        super(ID, target, nativeEvent);
    }
}