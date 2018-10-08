package com.tibco.customwizard.internal.console;

import org.nuxeo.xforms.ui.UIBuilder;

import com.tibco.customwizard.internal.xforms.BaseXFormExtension;

public class ConsoleXFormExtension extends BaseXFormExtension {
	public UIBuilder getUIBuilder() {
		ConsoleUIBuilder uiBuilder = new ConsoleUIBuilder(ConsoleRegistry.getInstance());
		return uiBuilder;
	}
}
