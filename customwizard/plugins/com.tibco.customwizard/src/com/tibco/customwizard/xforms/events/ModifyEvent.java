package com.tibco.customwizard.xforms.events;

import org.nuxeo.xforms.ui.AbstractUIControl;
import org.nuxeo.xforms.xforms.events.UIControlEvent;

public class ModifyEvent extends UIControlEvent {

	public final static String ID = "modify";

	public ModifyEvent(AbstractUIControl target, Object nativeEvent) {
		super(ID, target, nativeEvent);
	}

}
