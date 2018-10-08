package com.tibco.customwizard.internal.support;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IStatus;

import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.config.IErrorHandler;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.MessageType;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;

public class DefaultErrorHandler implements IErrorHandler {
	public void handleError(WizardInstance wizardInstance, Throwable ex) {
		Throwable cause = ex;
		if (ex instanceof InvocationTargetException) {
			if (ex.getCause() != null) {
				cause = ex.getCause();
			}
		}
		String errorMessage = cause.toString();
		if (cause instanceof ActionException) {
			errorMessage = cause.getMessage();
		}

		IStatus status = WizardHelper.logException(errorMessage, ex);
		
		if (WizardHelper.isSWTMode(wizardInstance) && SWTHelper.getWizard(wizardInstance) != null) {
			SWTHelper.setWizardMessage(wizardInstance, errorMessage, SWTHelper.convertMessageType(MessageType.ERROR), false);
			ex.printStackTrace();
		} else {
			WizardHelper.openErrorDialog(wizardInstance, errorMessage, status);
		}
	}
}
