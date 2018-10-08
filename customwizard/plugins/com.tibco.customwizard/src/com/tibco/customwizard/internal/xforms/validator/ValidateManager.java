package com.tibco.customwizard.internal.xforms.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.events.XFormsInvalidEvent;
import org.nuxeo.xforms.xforms.events.XFormsValidEvent;
import org.nuxeo.xforms.xforms.model.controls.XFControlElement;
import org.nuxeo.xforms.xsd.XSDValidationException;

import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.customwizard.validator.ICustomValidator;
import com.tibco.customwizard.validator.ValidateResult;

public class ValidateManager {
    private static final String RESULT_MAP_PROPERTY = "validator.result";

    private static final String VALIDATOR_MAP_PROPERTY = "validator.object";

    public static void setValidateResult(UIControl key, ValidateResult validateResult) {
    	getResultMap(key).put(key, validateResult);
    }

    public static ValidateResult getValidateResult(UIControl key) {
        return getResultMap(key).get(key);
    }

    public static void addValidator(XFControlElement key, ICustomValidator validator) {
        List<ICustomValidator> validatorList = getValidatorMap(key).get(key);
        if (validatorList == null) {
            validatorList = new ArrayList<ICustomValidator>();
            getValidatorMap(key).put(key, validatorList);
        }
        validatorList.add(validator);
    }

    public static List<ICustomValidator> getValidatorList(XFControlElement key) {
        return getValidatorMap(key).get(key);
    }

    @SuppressWarnings("unchecked")
	private static Map<UIControl, ValidateResult> getResultMap(UIControl control) {
		WizardInstance wizardInstance = XFormUtils.getWizardInstance(control);
		Map<UIControl, ValidateResult> resultMap = (Map<UIControl, ValidateResult>) wizardInstance.getAttribute(RESULT_MAP_PROPERTY);
		if (resultMap == null) {
			resultMap = new HashMap<UIControl, ValidateResult>();
			wizardInstance.setAttribute(RESULT_MAP_PROPERTY, resultMap);
		}
		return resultMap;
	}

    @SuppressWarnings("unchecked")
	private static Map<XFControlElement, List<ICustomValidator>> getValidatorMap(XFControlElement controlElement) {
    	WizardInstance wizardInstance = XFormUtils.getWizardInstance(controlElement.getForm());
		Map<XFControlElement, List<ICustomValidator>> validatorMap = (Map<XFControlElement, List<ICustomValidator>>) wizardInstance
				.getAttribute(VALIDATOR_MAP_PROPERTY);
		if (validatorMap == null) {
			validatorMap = new HashMap<XFControlElement, List<ICustomValidator>>();
			wizardInstance.setAttribute(VALIDATOR_MAP_PROPERTY, validatorMap);
		}
		return validatorMap;
	}

	public static ValidateResult validateControl(UIControl control) {
        // xform schema validation
        try {
            control.validate();
            control.getElement().getForm().getProcessor().dispatch(new XFormsValidEvent(control));
        } catch (XSDValidationException e) {
            e.setData(control);
            control.getElement().getForm().getProcessor().dispatch(new XFormsInvalidEvent(control, e));
        }
        
        //custom validator
        List<ICustomValidator> validatorList = ValidateManager.getValidatorList(control.getElement());
        if (validatorList != null) {
            for (ICustomValidator validator : validatorList) {
                ValidateResult validateResult = validator.validate(new BaseXFormValidationContext(control));
                if (validateResult != null && !validateResult.isValid()) {
                    return validateResult;
                }
            }
        }
        return ValidateResult.VALID;
    }
}
