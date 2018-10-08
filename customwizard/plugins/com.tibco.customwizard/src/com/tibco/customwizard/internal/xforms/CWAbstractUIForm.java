package com.tibco.customwizard.internal.xforms;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.xforms.ui.AbstractUIControl;
import org.nuxeo.xforms.ui.AbstractUIForm;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xsd.XSDValidationException;

import com.tibco.customwizard.internal.xforms.validator.ValidateManager;
import com.tibco.customwizard.support.IValidatable;
import com.tibco.customwizard.validator.ValidateResult;

public abstract class CWAbstractUIForm extends AbstractUIForm implements IValidatable {
	private List<UIControl> controlList = new ArrayList<UIControl>();
	
	public CWAbstractUIForm(XForm form) {
		super(form);
	}

	public void validate() throws XSDValidationException {
		for (UIControl control : controlList) {
			ValidateManager.setValidateResult(control, ValidateManager.validateControl(control));
		}
	}

	public ValidateResult getValidateResult() {
		for (UIControl control : controlList) {
			ValidateResult validateResult = ValidateManager.getValidateResult(control);
			if (validateResult != null && !validateResult.isValid()) {
				return validateResult;
			}
		}
		return ValidateResult.VALID;
	}
	
	public List<UIControl> getControlList() {
		return controlList;
	}

	public void registerControlProxy(AbstractUIControl proxy) {
		controlList.add(proxy);
	}
}
