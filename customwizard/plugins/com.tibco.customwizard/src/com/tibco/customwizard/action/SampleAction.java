package com.tibco.customwizard.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class SampleAction implements ICustomAction {
    public void execute(IActionContext actionContext) throws Exception {
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "", "Sample action");
    }
}
