package com.tibco.configtool.actions;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.configtool.utils.URLParser;
import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.util.ControlUtils;
import com.tibco.customwizard.validator.ICustomValidator;
import com.tibco.customwizard.validator.IValidationContext;
import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.IXFormValidationContext;

public class DBUrlValidator implements ICustomValidator {
	public ValidateResult validate(IValidationContext validationContext) {
		UIControl urlControl = ((IXFormValidationContext) validationContext).getControl();
		if (ControlUtils.isEnabled(urlControl)) {
			XForm xform = ((IXFormValidationContext) validationContext).getForm();

			String url = urlControl.getXMLValue();
			try {
				URLParser.parse(url);
			} catch (Exception e) {
				return new ValidateResult(e.getMessage());
			}

			if (xform.getUI().getControl("sslcontrol") != null) {
				Boolean sslEnable = Boolean.parseBoolean(xform.getUI().getControl("sslcontrol").getXMLValue());
				if (sslEnable) {
					if (url.startsWith("jdbc:oracle:") && (url.indexOf("jdbc:oracle:thin:@(") < 0)) {
						try {
							IMessageProvider messageProvider = TCTHelper.getMessageProvider(validationContext.getWizardInstance());
							return new ValidateResult(messageProvider.getMessage("oracle.error.ssl.url"));
						} catch (Exception e) {
							throw new ActionException(e);
						}
					}
				}
			}
		}

		return ValidateResult.VALID;
	}
}
