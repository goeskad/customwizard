package com.tibco.customwizard.internal.xforms.validator;

import java.io.File;

import org.nuxeo.xforms.ui.UIControl;

import com.tibco.customwizard.util.ControlUtils;
import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.IXFormValidationContext;

public class SimpleControlValidator extends XFormControlValidator {
    private String[] validateRules;

    public SimpleControlValidator(String validation) {
        if (validation != null) {
            validateRules = validation.split(",");
        }
    }

    public ValidateResult validate(UIControl control, IXFormValidationContext validationContext) {
		if (validateRules != null && ControlUtils.isEnabled(control)) {
            String fieldName = control.getName();
            String value = control.getXMLValue();
            for (String validateRule : validateRules) {
                String errorMsg = validate(control, validateRule, fieldName, value);
                if (errorMsg != null) {
                    return new ValidateResult(errorMsg);
                }
            }
        }

        return ValidateResult.VALID;
    }

    private String validate(UIControl control, String validateRule, String fieldName, String value) {
        if (validateRule.equals("nonempty")) {
            if (value == null || value.equals("")) {
                return fieldName + " must be nonempty.";
            }
		} else if (validateRule.equals("noblank")) {
			if (value.contains(" ")) {
				return fieldName + " must not contain blank.";
			}
		} else if (validateRule.equals("number")) {
            if (value != null && value.length() > 0 && !value.matches("-?[0-9]*.?[0-9]+")) {
                return fieldName + " must be a number.";
            }
        } else if (validateRule.equals("url")) {
            String urlRegex = "^(https?://)" + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // user@
                    + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP-
                    // 199.194.52.184
                    + "|" // allows either IP or domain
                    + "([0-9a-z_!~*'()-]+\\.)*" // tertiary domain(s)- www.
                    + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // second
                    // level
                    // domain
                    + "[a-z]{2,6})" // first level domain- .com or .museum
                    + "(:[0-9]{1,4})?" // port number- :80
                    + "((/?)|" // a slash isn't required if there is no
                    // file
                    // name
                    + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
            if (value == null || !value.matches(urlRegex)) {
                return fieldName + " must be a url.";
            }
        } else if (validateRule.equals("true") || validateRule.equals("TRUE")) {
            if (value == null || (!value.equals("true") && !value.equals("TRUE") && !value.equals("1"))) {
                return fieldName + " must be checked.";
            }
        } else if (validateRule.equals("exist")) {
            if (value != null && value.length() > 0) {
                File file = new File(value);
                if (!file.exists()) {
                    return "The file " + file.getName() + " does not exist.";
                }
            }
        }

        return null;
    }
}
