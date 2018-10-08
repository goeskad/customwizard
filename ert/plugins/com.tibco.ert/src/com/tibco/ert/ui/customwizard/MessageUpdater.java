package com.tibco.ert.ui.customwizard;

import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.util.WizardHelper;

public class MessageUpdater {
    public static void setMessage(WizardConfig wizardConfig, String newMessage) {
        WizardHelper.syncSetMessage(wizardConfig, newMessage);
    }
}
