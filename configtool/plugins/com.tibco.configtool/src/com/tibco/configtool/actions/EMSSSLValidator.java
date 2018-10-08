package com.tibco.configtool.actions;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.validator.ICustomValidator;
import com.tibco.customwizard.validator.IValidationContext;
import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.IXFormValidationContext;

public class EMSSSLValidator implements ICustomValidator {
	public ValidateResult validate(IValidationContext validationContext) {
		XForm xform = ((IXFormValidationContext) validationContext).getForm();
		Boolean sslEnable = Boolean.parseBoolean(xform.getUI().getControl("sslcontrol").getXMLValue());
		String[] hostportList = xform.getUI().getControl("hostportlist").getXMLValue().split(",");

		try {
			IMessageProvider messageProvider = TCTHelper.getMessageProvider(validationContext.getWizardInstance());

			for (String hostport : hostportList) {
				String[] tmp = hostport.split(":");
				if (tmp.length != 2) {
					return new ValidateResult(messageProvider.getMessage("EMSSSLValidator.error.invalid"));
				}
				String port = tmp[1];
				if (sslEnable) {
					if (!port.equals("7243")) {
						return new ValidateResult(messageProvider.getMessage("EMSSSLValidator.error.port.whensslenable"));
					}
				}
			}
		} catch (Exception e) {
			throw new ActionException(e);
		}
		
		return ValidateResult.VALID;
	}
}
